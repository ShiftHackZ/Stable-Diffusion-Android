#include "bonsai_model_probe.h"
#include "bonsai_runtime.h"

#include <jni.h>

#include <android/log.h>

#include <atomic>
#include <exception>
#include <string>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";
constexpr const char* BRIDGE_CLASS =
    "com/shifthackz/aisdv1/feature/bonsai/BonsaiNativeBridge";
constexpr const char* CALLBACK_CLASS =
    "com/shifthackz/aisdv1/feature/bonsai/BonsaiNativeBridge$ProgressCallback";

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

    std::string str() const {
        return chars_ == nullptr ? "" : std::string(chars_);
    }

private:
    JNIEnv* env_;
    jstring value_;
    const char* chars_ = nullptr;
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

void emit_progress(JNIEnv* env, jobject callback, jint current, jint total) {
    if (callback == nullptr) {
        return;
    }

    jclass callback_class = env->FindClass(CALLBACK_CLASS);
    if (callback_class == nullptr) {
        return;
    }

    jmethodID on_progress = env->GetMethodID(callback_class, "onProgress", "(II)V");
    if (on_progress == nullptr) {
        return;
    }

    env->CallVoidMethod(callback, on_progress, current, total);
}

jstring native_probe_model(
    JNIEnv* env,
    jobject,
    jstring root_path,
    jstring packed_transformer_path,
    jstring text_encoder_path,
    jstring tokenizer_path,
    jstring vae_path,
    jstring scheduler_path
) {
    const JStringChars root_path_chars(env, root_path);
    const JStringChars packed_transformer_path_chars(env, packed_transformer_path);
    const JStringChars text_encoder_path_chars(env, text_encoder_path);
    const JStringChars tokenizer_path_chars(env, tokenizer_path);
    const JStringChars vae_path_chars(env, vae_path);
    const JStringChars scheduler_path_chars(env, scheduler_path);

    try {
        const std::string summary = probe_bonsai_model(
            BonsaiModelPaths {
                root_path_chars.str(),
                packed_transformer_path_chars.str(),
                text_encoder_path_chars.str(),
                tokenizer_path_chars.str(),
                vae_path_chars.str(),
                scheduler_path_chars.str(),
            }
        );
        __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "model probe %s", summary.c_str());
        return env->NewStringUTF(summary.c_str());
    } catch (const std::exception& error) {
        throw_illegal_state(env, error.what());
        return nullptr;
    }
}

jstring native_generate(
    JNIEnv* env,
    jobject,
    jstring root_path,
    jstring packed_transformer_path,
    jstring text_encoder_path,
    jstring tokenizer_path,
    jstring vae_path,
    jstring scheduler_path,
    jstring prompt,
    jstring negative_prompt,
    jint sampling_steps,
    jfloat cfg_scale,
    jint width,
    jint height,
    jstring seed,
    jint batch_count,
    jboolean allow_nsfw,
    jstring backend,
    jobject callback
) {
    g_cancel_requested.store(false);
    const JStringChars root_path_chars(env, root_path);
    const JStringChars packed_transformer_path_chars(env, packed_transformer_path);
    const JStringChars text_encoder_path_chars(env, text_encoder_path);
    const JStringChars tokenizer_path_chars(env, tokenizer_path);
    const JStringChars vae_path_chars(env, vae_path);
    const JStringChars scheduler_path_chars(env, scheduler_path);
    const JStringChars prompt_chars(env, prompt);
    const JStringChars negative_prompt_chars(env, negative_prompt);
    const JStringChars seed_chars(env, seed);
    const JStringChars backend_chars(env, backend);

    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "request rootPath=%s transformerPath=%s textEncoderPath=%s vaePath=%s size=%dx%d steps=%d cfg=%.3f batch=%d allowNsfw=%s backend=%s seedBlank=%s promptChars=%zu negativeChars=%zu",
        root_path_chars.str().c_str(),
        packed_transformer_path_chars.str().c_str(),
        text_encoder_path_chars.str().c_str(),
        vae_path_chars.str().c_str(),
        width,
        height,
        sampling_steps,
        cfg_scale,
        batch_count,
        allow_nsfw == JNI_TRUE ? "true" : "false",
        backend_chars.str().c_str(),
        seed_chars.str().empty() ? "true" : "false",
        prompt_chars.str().size(),
        negative_prompt_chars.str().size()
    );

    try {
        const std::string output = bonsai_generate_image(
            BonsaiGenerationRequest {
                BonsaiModelPaths {
                    root_path_chars.str(),
                    packed_transformer_path_chars.str(),
                    text_encoder_path_chars.str(),
                    tokenizer_path_chars.str(),
                    vae_path_chars.str(),
                    scheduler_path_chars.str(),
                },
                prompt_chars.str(),
                negative_prompt_chars.str(),
                sampling_steps,
                cfg_scale,
                width,
                height,
                seed_chars.str(),
                batch_count,
                allow_nsfw == JNI_TRUE,
                backend_chars.str(),
            },
            [env, callback](int current, int total) {
                emit_progress(env, callback, current, total);
            },
            g_cancel_requested
        );
        return env->NewStringUTF(output.c_str());
    } catch (const BonsaiGenerationCancelled& error) {
        throw_java(env, "java/util/concurrent/CancellationException", error.what());
        return nullptr;
    } catch (const std::exception& error) {
        throw_illegal_state(env, error.what());
        return nullptr;
    }
}

void native_interrupt(JNIEnv*, jobject) {
    g_cancel_requested.store(true);
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "interrupt requested");
}

JNINativeMethod g_methods[] = {
    {
        const_cast<char*>("probeModel"),
        const_cast<char*>(
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;"
        ),
        reinterpret_cast<void*>(native_probe_model),
    },
    {
        const_cast<char*>("generateModel"),
        const_cast<char*>(
            "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;IFIILjava/lang/String;IZLjava/lang/String;Lcom/shifthackz/aisdv1/feature/bonsai/BonsaiNativeBridge$ProgressCallback;)Ljava/lang/String;"
        ),
        reinterpret_cast<void*>(native_generate),
    },
    {
        const_cast<char*>("interrupt"),
        const_cast<char*>("()V"),
        reinterpret_cast<void*>(native_interrupt),
    },
};

} // namespace

JNIEXPORT jint JNI_OnLoad(JavaVM* vm, void*) {
    JNIEnv* env = nullptr;
    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK || env == nullptr) {
        return JNI_ERR;
    }

    jclass bridge_class = env->FindClass(BRIDGE_CLASS);
    if (bridge_class == nullptr) {
        return JNI_ERR;
    }

    if (env->RegisterNatives(
            bridge_class,
            g_methods,
            sizeof(g_methods) / sizeof(g_methods[0])
        ) != JNI_OK) {
        return JNI_ERR;
    }

    return JNI_VERSION_1_6;
}
