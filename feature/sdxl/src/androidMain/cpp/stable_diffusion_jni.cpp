#include <jni.h>

#include <android/log.h>
#include <ggml-backend.h>
#include <stable-diffusion.h>

#include <algorithm>
#include <atomic>
#include <cctype>
#include <cstdint>
#include <cstdlib>
#include <mutex>
#include <sstream>
#include <stdexcept>
#include <string>

namespace {

constexpr const char* LOG_TAG = "SDAI-SDCPP";
constexpr const char* BRIDGE_CLASS =
    "com/shifthackz/aisdv1/feature/sdxl/StableDiffusionCppNativeBridge";
constexpr const char* CALLBACK_CLASS =
    "com/shifthackz/aisdv1/feature/sdxl/StableDiffusionCppNativeBridge$ProgressCallback";

JavaVM* g_vm = nullptr;
std::mutex g_context_mutex;
std::mutex g_log_mutex;
sd_ctx_t* g_context = nullptr;
std::string g_context_key;
std::string g_last_error;
std::atomic_bool g_cancel_requested(false);

class JStringChars {
public:
    JStringChars(JNIEnv* env, jstring value) : env_(env), value_(value) {
        if (value_ != nullptr) {
            chars_ = env_->GetStringUTFChars(value_, nullptr);
        }
    }

    ~JStringChars() {
        if (value_ != nullptr && chars_ != nullptr) {
            env_->ReleaseStringUTFChars(value_, chars_);
        }
    }

    const char* c_str() const {
        return chars_ == nullptr ? "" : chars_;
    }

    std::string str() const {
        return std::string(c_str());
    }

private:
    JNIEnv* env_;
    jstring value_;
    const char* chars_ = nullptr;
};

struct ProgressCallbackState {
    jobject callback = nullptr;
    jmethodID on_progress = nullptr;
};

void throw_java(JNIEnv* env, const char* class_name, const std::string& message) {
    jclass clazz = env->FindClass(class_name);
    if (clazz != nullptr) {
        env->ThrowNew(clazz, message.c_str());
    }
}

void throw_illegal_state(JNIEnv* env, const std::string& message) {
    throw_java(env, "java/lang/IllegalStateException", message);
}

void throw_cancelled(JNIEnv* env) {
    throw_java(env, "java/util/concurrent/CancellationException", "Generation cancelled.");
}

void configure_opencl_icd_loader() {
#if SDAI_SDCPP_HAS_OPENCL
    const char* current = std::getenv("OCL_ICD_FILENAMES");
    if (current != nullptr && current[0] != '\0') {
        return;
    }

    // Android vendors commonly ship OpenCL as a private driver library without
    // an ICD file. Let the Khronos ICD loader try the usual 64-bit locations.
    setenv(
        "OCL_ICD_FILENAMES",
        "/vendor/lib64/libOpenCL.so:"
        "/system/vendor/lib64/libOpenCL.so:"
        "/vendor/lib64/egl/libGLES_mali.so:"
        "/system/vendor/lib64/egl/libGLES_mali.so",
        0
    );
#endif
}

bool validate_backend_available(JNIEnv* env, jint backend) {
    if (backend == 2 && !SDAI_SDCPP_HAS_OPENCL) {
        throw_illegal_state(
            env,
            "OpenCL backend is not compiled into this build. "
            "Android OpenCL support requires building stable-diffusion.cpp with "
            "SDAI_SDCPP_ENABLE_OPENCL=ON and providing OpenCL headers plus libOpenCL.so."
        );
        return false;
    }
    if (backend == 3 && !SDAI_SDCPP_HAS_VULKAN) {
        throw_illegal_state(env, "Vulkan backend is not compiled into this build.");
        return false;
    }
    return true;
}

std::string backend_spec_for(jint backend) {
    switch (backend) {
        case 1:
            return "cpu";
        case 2:
            return "diffusion=opencl0,te=cpu,vae=cpu";
        case 3:
            return "diffusion=vulkan0,te=cpu,vae=cpu";
        default:
            return "";
    }
}

std::string lower_copy(std::string value) {
    std::transform(value.begin(), value.end(), value.begin(), [](unsigned char c) {
        return static_cast<char>(std::tolower(c));
    });
    return value;
}

bool is_backend_request(const std::string& backend_spec, const std::string& backend_name) {
    return lower_copy(backend_spec).find(backend_name) != std::string::npos;
}

bool is_backend_not_found_error(const std::string& details, const std::string& backend_name) {
    const std::string lower = lower_copy(details);
    return lower.find("backend '" + backend_name) != std::string::npos &&
        lower.find("was not found") != std::string::npos;
}

const char* backend_device_type_name(enum ggml_backend_dev_type type) {
    switch (type) {
        case GGML_BACKEND_DEVICE_TYPE_CPU:
            return "CPU";
        case GGML_BACKEND_DEVICE_TYPE_GPU:
            return "GPU";
        case GGML_BACKEND_DEVICE_TYPE_IGPU:
            return "IGPU";
        case GGML_BACKEND_DEVICE_TYPE_ACCEL:
            return "ACCEL";
        case GGML_BACKEND_DEVICE_TYPE_META:
            return "META";
    }
    return "UNKNOWN";
}

std::string available_backend_devices() {
    const size_t count = ggml_backend_dev_count();
    if (count == 0) {
        return "No ggml backend devices were registered.";
    }

    std::ostringstream devices;
    devices << "Available ggml backend devices:";
    for (size_t i = 0; i < count; ++i) {
        ggml_backend_dev_t dev = ggml_backend_dev_get(i);
        ggml_backend_dev_props props{};
        ggml_backend_dev_get_props(dev, &props);
        const char* name = props.name == nullptr ? ggml_backend_dev_name(dev) : props.name;
        devices << " #" << i << "=" << (name == nullptr ? "unknown" : name)
            << "(" << backend_device_type_name(props.type) << ")";
    }
    return devices.str();
}

std::string stable_diffusion_context_error(
    const std::string& details,
    const std::string& backend_spec
) {
    if (is_backend_request(backend_spec, "opencl") && is_backend_not_found_error(details, "opencl")) {
        return "OpenCL backend is not available on this device or driver. "
            "stable-diffusion.cpp reported: " + details + ". "
            "This usually means ggml-opencl could not register an OpenCL-capable device. "
            + available_backend_devices() + " Use CPU or Auto backend instead.";
    }
    if (is_backend_request(backend_spec, "vulkan") && is_backend_not_found_error(details, "vulkan")) {
        return "Vulkan backend is not available on this device or driver. "
            "stable-diffusion.cpp reported: " + details + ". "
            "This usually means ggml-vulkan could not register a Vulkan 1.2 compatible device. "
            + available_backend_devices() + " Use CPU or Auto backend instead.";
    }
    return details.empty()
        ? "Failed to initialize stable-diffusion.cpp context."
        : "Failed to initialize stable-diffusion.cpp context: " + details;
}

sample_method_t sample_method_for(jint sampler) {
    switch (sampler) {
        case 0:
            return EULER_A_SAMPLE_METHOD;
        case 1:
            return EULER_SAMPLE_METHOD;
        case 2:
            return HEUN_SAMPLE_METHOD;
        case 3:
            return DPM2_SAMPLE_METHOD;
        case 4:
            return DPMPP2S_A_SAMPLE_METHOD;
        case 5:
            return DPMPP2M_SAMPLE_METHOD;
        case 6:
            return IPNDM_SAMPLE_METHOD;
        case 7:
            return LCM_SAMPLE_METHOD;
        case 8:
            return DDIM_TRAILING_SAMPLE_METHOD;
        case 9:
            return TCD_SAMPLE_METHOD;
        default:
            return EULER_SAMPLE_METHOD;
    }
}

int android_log_priority(sd_log_level_t level) {
    switch (level) {
        case SD_LOG_DEBUG:
            return ANDROID_LOG_DEBUG;
        case SD_LOG_INFO:
            return ANDROID_LOG_INFO;
        case SD_LOG_WARN:
            return ANDROID_LOG_WARN;
        case SD_LOG_ERROR:
            return ANDROID_LOG_ERROR;
        default:
            return ANDROID_LOG_INFO;
    }
}

void clear_last_error() {
    std::lock_guard<std::mutex> lock(g_log_mutex);
    g_last_error.clear();
}

std::string last_error() {
    std::lock_guard<std::mutex> lock(g_log_mutex);
    return g_last_error;
}

void log_callback(sd_log_level_t level, const char* text, void* data) {
    const char* message = text == nullptr ? "" : text;
    if (level == SD_LOG_ERROR) {
        std::lock_guard<std::mutex> lock(g_log_mutex);
        if (!g_last_error.empty()) {
            g_last_error += " ";
        }
        g_last_error += message;
        if (g_last_error.size() > 512) {
            g_last_error = g_last_error.substr(g_last_error.size() - 512);
        }
    }
    __android_log_print(android_log_priority(level), LOG_TAG, "%s", message);
}

JNIEnv* get_env(bool* attached) {
    *attached = false;
    JNIEnv* env = nullptr;
    if (g_vm == nullptr) {
        return nullptr;
    }

    const jint status = g_vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6);
    if (status == JNI_OK) {
        return env;
    }
    if (status == JNI_EDETACHED && g_vm->AttachCurrentThread(&env, nullptr) == JNI_OK) {
        *attached = true;
        return env;
    }
    return nullptr;
}

void progress_callback(int step, int steps, float time, void* data) {
    auto* state = static_cast<ProgressCallbackState*>(data);
    if (state == nullptr || state->callback == nullptr || state->on_progress == nullptr) {
        return;
    }

    bool attached = false;
    JNIEnv* env = get_env(&attached);
    if (env == nullptr) {
        return;
    }

    env->CallVoidMethod(state->callback, state->on_progress, step, steps);

    if (attached) {
        g_vm->DetachCurrentThread();
    }
}

void free_context() {
    if (g_context != nullptr) {
        free_sd_ctx(g_context);
        g_context = nullptr;
        g_context_key.clear();
    }
}

sd_ctx_t* get_or_create_context(
    const std::string& model_path,
    const std::string& backend_spec,
    JNIEnv* env
) {
    const std::string params_backend = backend_spec.empty() || backend_spec == "cpu" ? "" : "cpu";
    const std::string context_key = model_path + "|" + backend_spec + "|" + params_backend;
    if (g_context != nullptr && g_context_key == context_key) {
        return g_context;
    }

    free_context();

    sd_ctx_params_t ctx_params;
    sd_ctx_params_init(&ctx_params);
    ctx_params.model_path = model_path.c_str();
    ctx_params.n_threads = std::max(1, sd_get_num_physical_cores());
    ctx_params.rng_type = CPU_RNG;
    ctx_params.sampler_rng_type = CPU_RNG;
    ctx_params.enable_mmap = true;
    ctx_params.flash_attn = true;
    ctx_params.diffusion_flash_attn = true;
    ctx_params.backend = backend_spec.empty() ? nullptr : backend_spec.c_str();
    ctx_params.params_backend = params_backend.empty() ? nullptr : params_backend.c_str();

    clear_last_error();
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "Initializing context for '%s'", model_path.c_str());
    g_context = new_sd_ctx(&ctx_params);
    if (g_context == nullptr) {
        const std::string details = last_error();
        throw_illegal_state(env, stable_diffusion_context_error(details, backend_spec));
        return nullptr;
    }
    if (!sd_ctx_supports_image_generation(g_context)) {
        free_context();
        throw_illegal_state(env, "The selected model does not support image generation.");
        return nullptr;
    }

    g_context_key = context_key;
    return g_context;
}

jbyteArray copy_first_image(JNIEnv* env, sd_image_t* images, jint batch_count, jintArray shape) {
    if (images == nullptr || images[0].data == nullptr) {
        throw_illegal_state(env, "stable-diffusion.cpp returned an empty image.");
        return nullptr;
    }

    const sd_image_t& image = images[0];
    const int64_t byte_count = static_cast<int64_t>(image.width) *
        static_cast<int64_t>(image.height) *
        static_cast<int64_t>(image.channel);
    if (byte_count <= 0 || byte_count > INT32_MAX) {
        throw_illegal_state(env, "stable-diffusion.cpp returned an invalid image payload.");
        return nullptr;
    }

    jint shape_values[3] = {
        static_cast<jint>(image.width),
        static_cast<jint>(image.height),
        static_cast<jint>(image.channel),
    };
    env->SetIntArrayRegion(shape, 0, 3, shape_values);

    jbyteArray result = env->NewByteArray(static_cast<jsize>(byte_count));
    if (result == nullptr) {
        return nullptr;
    }
    env->SetByteArrayRegion(
        result,
        0,
        static_cast<jsize>(byte_count),
        reinterpret_cast<const jbyte*>(image.data)
    );
    return result;
}

jbyteArray native_generate(
    JNIEnv* env,
    jobject thiz,
    jstring model_path,
    jstring prompt,
    jstring negative_prompt,
    jint width,
    jint height,
    jint sampling_steps,
    jfloat cfg_scale,
    jlong seed,
    jint batch_count,
    jint backend,
    jint sampler,
    jintArray shape,
    jobject callback
) {
    if (shape == nullptr || env->GetArrayLength(shape) < 3) {
        throw_illegal_state(env, "Native output shape must contain at least 3 items.");
        return nullptr;
    }

    JStringChars model_path_chars(env, model_path);
    JStringChars prompt_chars(env, prompt);
    JStringChars negative_prompt_chars(env, negative_prompt);

    if (!validate_backend_available(env, backend)) {
        return nullptr;
    }

    std::lock_guard<std::mutex> lock(g_context_mutex);
    g_cancel_requested = false;

    sd_ctx_t* context = get_or_create_context(model_path_chars.str(), backend_spec_for(backend), env);
    if (context == nullptr || env->ExceptionCheck()) {
        return nullptr;
    }

    jclass callback_class = env->FindClass(CALLBACK_CLASS);
    if (callback_class == nullptr) {
        return nullptr;
    }

    ProgressCallbackState callback_state;
    callback_state.callback = env->NewGlobalRef(callback);
    callback_state.on_progress = env->GetMethodID(callback_class, "onProgress", "(II)V");

    if (callback_state.callback == nullptr || callback_state.on_progress == nullptr) {
        if (callback_state.callback != nullptr) {
            env->DeleteGlobalRef(callback_state.callback);
        }
        throw_illegal_state(env, "Unable to initialize stable-diffusion.cpp progress callback.");
        return nullptr;
    }

    sd_set_progress_callback(progress_callback, &callback_state);

    sd_img_gen_params_t gen_params;
    sd_img_gen_params_init(&gen_params);
    gen_params.prompt = prompt_chars.c_str();
    gen_params.negative_prompt = negative_prompt_chars.c_str();
    gen_params.width = width;
    gen_params.height = height;
    gen_params.seed = seed;
    gen_params.batch_count = std::max(1, static_cast<int>(batch_count));
    gen_params.sample_params.sample_steps = std::max(1, static_cast<int>(sampling_steps));
    gen_params.sample_params.guidance.txt_cfg = cfg_scale;
    gen_params.sample_params.sample_method = sample_method_for(sampler);
    gen_params.sample_params.scheduler = sd_get_default_scheduler(
        context,
        gen_params.sample_params.sample_method
    );
    gen_params.vae_tiling_params.enabled = true;
    gen_params.vae_tiling_params.tile_size_x = 512;
    gen_params.vae_tiling_params.tile_size_y = 512;
    gen_params.vae_tiling_params.target_overlap = 0.5f;

    sd_image_t* images = generate_image(context, &gen_params);
    sd_set_progress_callback(nullptr, nullptr);

    env->DeleteGlobalRef(callback_state.callback);

    if (g_cancel_requested) {
        free_sd_images(images, gen_params.batch_count);
        throw_cancelled(env);
        return nullptr;
    }

    jbyteArray result = copy_first_image(env, images, gen_params.batch_count, shape);
    free_sd_images(images, gen_params.batch_count);
    return result;
}

void native_interrupt(JNIEnv* env, jobject thiz) {
    g_cancel_requested = true;
}

}  // namespace

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void* reserved) {
    g_vm = vm;
    configure_opencl_icd_loader();

    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) {
        return JNI_ERR;
    }

    JNINativeMethod methods[] = {
        {
            const_cast<char*>("generate"),
            const_cast<char*>(
                "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IIIFJIII[ILcom/shifthackz/aisdv1/feature/sdxl/StableDiffusionCppNativeBridge$ProgressCallback;)[B"
            ),
            reinterpret_cast<void*>(native_generate),
        },
        {
            const_cast<char*>("interrupt"),
            const_cast<char*>("()V"),
            reinterpret_cast<void*>(native_interrupt),
        },
    };

    jclass bridge_class = env->FindClass(BRIDGE_CLASS);
    if (bridge_class == nullptr) {
        return JNI_ERR;
    }

    if (env->RegisterNatives(
            bridge_class,
            methods,
            sizeof(methods) / sizeof(methods[0])
        ) != JNI_OK) {
        return JNI_ERR;
    }

    sd_set_log_callback(log_callback, nullptr);
    return JNI_VERSION_1_6;
}
