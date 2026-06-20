#include "bonsai_runtime.h"

#include "bonsai_image_encoder.h"
#include "bonsai_latents.h"
#include "bonsai_prompt.h"
#include "bonsai_qwen_inputs.h"
#include "bonsai_runtime_context.h"
#include "bonsai_scheduler.h"
#include "bonsai_vulkan.h"

#include <android/log.h>

#include <array>
#include <cctype>
#include <cmath>
#include <dlfcn.h>
#include <fstream>
#include <limits>
#include <malloc.h>
#include <memory>
#include <random>
#include <sstream>
#include <vector>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";
constexpr uint64_t GIB_KB = 1024ULL * 1024ULL;
constexpr uint64_t PERFORMANCE_RAM_KB = 7ULL * GIB_KB;
constexpr uint64_t BALANCED_RAM_KB = 5ULL * GIB_KB;

enum class BonsaiMemoryPolicy {
    Performance,
    Balanced,
    Survival,
};

struct BonsaiTextPhaseOutput {
    BonsaiQwenInputShell input_shell;
    BonsaiQwenPromptEmbeddings prompt_embeddings;
    BonsaiQwenPromptEmbeddings negative_prompt_embeddings;
};

std::string trim_ascii(const std::string& value);

void log_phase(const char* phase) {
    __android_log_print(ANDROID_LOG_INFO, LOG_TAG, "phase=%s", phase);
}

const char* memory_policy_name(BonsaiMemoryPolicy policy) {
    switch (policy) {
        case BonsaiMemoryPolicy::Performance:
            return "performance";
        case BonsaiMemoryPolicy::Balanced:
            return "balanced";
        case BonsaiMemoryPolicy::Survival:
            return "survival";
    }
    return "unknown";
}

uint64_t android_total_ram_kb() {
    std::ifstream meminfo("/proc/meminfo");
    std::string line;
    while (std::getline(meminfo, line)) {
        if (line.rfind("MemTotal:", 0) != 0) {
            continue;
        }
        std::istringstream stream(line);
        std::string label;
        uint64_t value = 0;
        std::string unit;
        stream >> label >> value >> unit;
        return value;
    }
    return 0;
}

uint64_t process_status_value_kb(const char* key) {
    std::ifstream status("/proc/self/status");
    std::string line;
    const std::string prefix = std::string(key) + ":";
    while (std::getline(status, line)) {
        if (line.rfind(prefix, 0) != 0) {
            continue;
        }
        std::istringstream stream(line);
        std::string label;
        uint64_t value = 0;
        std::string unit;
        stream >> label >> value >> unit;
        return value;
    }
    return 0;
}

BonsaiMemoryPolicy auto_memory_policy(uint64_t total_ram_kb) {
    if (total_ram_kb >= PERFORMANCE_RAM_KB) {
        return BonsaiMemoryPolicy::Performance;
    }
    if (total_ram_kb >= BALANCED_RAM_KB) {
        return BonsaiMemoryPolicy::Balanced;
    }
    return BonsaiMemoryPolicy::Survival;
}

void log_memory_snapshot(const char* point, BonsaiMemoryPolicy policy) {
    const uint64_t total_ram_kb = android_total_ram_kb();
    const uint64_t vm_rss_kb = process_status_value_kb("VmRSS");
    const uint64_t vm_hwm_kb = process_status_value_kb("VmHWM");
    const uint64_t vm_size_kb = process_status_value_kb("VmSize");
    const struct mallinfo2 info = mallinfo2();
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=memory_snapshot point=%s memory_policy=%s total_ram_mb=%llu vm_rss_mb=%llu vm_hwm_mb=%llu vm_size_mb=%llu heap_alloc_mb=%llu heap_free_mb=%llu heap_releasable_mb=%llu",
        point,
        memory_policy_name(policy),
        static_cast<unsigned long long>(total_ram_kb / 1024ULL),
        static_cast<unsigned long long>(vm_rss_kb / 1024ULL),
        static_cast<unsigned long long>(vm_hwm_kb / 1024ULL),
        static_cast<unsigned long long>(vm_size_kb / 1024ULL),
        static_cast<unsigned long long>(info.uordblks / (1024ULL * 1024ULL)),
        static_cast<unsigned long long>(info.fordblks / (1024ULL * 1024ULL)),
        static_cast<unsigned long long>(info.keepcost / (1024ULL * 1024ULL))
    );
}

void purge_android_allocator_pages() {
#ifdef M_PURGE
    using MalloptFunction = int (*)(int, int);
    void* symbol = dlsym(RTLD_DEFAULT, "mallopt");
    if (symbol == nullptr) {
        return;
    }
    reinterpret_cast<MalloptFunction>(symbol)(M_PURGE, 0);
#endif
}

void purge_allocator(const char* point, BonsaiMemoryPolicy policy) {
    log_memory_snapshot(point, policy);
    purge_android_allocator_pages();
    std::string after_point(point);
    after_point += "_after_purge";
    log_memory_snapshot(after_point.c_str(), policy);
}

void require_not_cancelled(const std::atomic_bool& cancel_requested) {
    if (cancel_requested.load()) {
        throw BonsaiGenerationCancelled();
    }
}

void validate_request(const BonsaiGenerationRequest& request) {
    if (request.model_paths.root_path.empty()) {
        throw std::runtime_error("Bonsai model path is required.");
    }
    if (request.prompt.empty()) {
        throw std::runtime_error("Prompt is required.");
    }
    if (request.sampling_steps <= 0) {
        throw std::runtime_error("Bonsai sampling steps must be positive.");
    }
    if (!std::isfinite(request.cfg_scale)) {
        throw std::runtime_error("Bonsai CFG scale must be finite.");
    }
    if (request.width <= 0 || request.height <= 0 ||
        request.width % 32 != 0 || request.height % 32 != 0) {
        throw std::runtime_error("Bonsai image size must be positive and divisible by 32.");
    }
    if (request.batch_count != 1) {
        throw std::runtime_error("Android Bonsai runtime supports batch_count=1 only.");
    }
}

BonsaiVulkanBackendMode parse_backend_mode(const std::string& value) {
    if (value == "cpu" || value == "CPU") {
        return BonsaiVulkanBackendMode::Cpu;
    }
    if (value == "vulkan" || value == "VULKAN") {
        return BonsaiVulkanBackendMode::Vulkan;
    }
    return BonsaiVulkanBackendMode::Auto;
}

std::string trim_ascii(const std::string& value) {
    size_t start = 0;
    while (start < value.size() &&
           std::isspace(static_cast<unsigned char>(value[start])) != 0) {
        start++;
    }

    size_t end = value.size();
    while (end > start &&
           std::isspace(static_cast<unsigned char>(value[end - 1])) != 0) {
        end--;
    }
    return value.substr(start, end - start);
}

int64_t parse_seed(const std::string& seed) {
    const std::string trimmed = trim_ascii(seed);
    if (trimmed.empty()) {
        std::random_device random_device;
        std::mt19937 generator(random_device());
        std::uniform_int_distribution<int32_t> distribution(
            0,
            std::numeric_limits<int32_t>::max()
        );
        return distribution(generator);
    }

    try {
        size_t consumed = 0;
        const int64_t value = std::stoll(trimmed, &consumed, 10);
        if (consumed != trimmed.size()) {
            throw std::invalid_argument("trailing seed characters");
        }
        return value;
    } catch (const std::exception&) {
        throw std::runtime_error("Bonsai seed is not a valid integer: " + seed);
    }
}

std::vector<std::array<float, 4>> float4_ids(
    const std::vector<int32_t>& ids,
    const char* label
) {
    if (ids.empty() || ids.size() % 4U != 0) {
        throw std::runtime_error(std::string("Bonsai ") + label + " ids shape mismatch.");
    }
    std::vector<std::array<float, 4>> output;
    output.reserve(ids.size() / 4U);
    for (size_t index = 0; index < ids.size(); index += 4U) {
        output.push_back({
            static_cast<float>(ids[index]),
            static_cast<float>(ids[index + 1U]),
            static_cast<float>(ids[index + 2U]),
            static_cast<float>(ids[index + 3U]),
        });
    }
    return output;
}

std::vector<float> classifier_free_guidance(
    const std::vector<float>& conditional,
    const std::vector<float>& unconditional,
    float guidance
) {
    if (conditional.size() != unconditional.size()) {
        throw std::runtime_error("Bonsai CFG noise shape mismatch.");
    }
    std::vector<float> output;
    output.reserve(conditional.size());
    for (size_t index = 0; index < conditional.size(); index++) {
        output.push_back(
            unconditional[index] + guidance * (conditional[index] - unconditional[index])
        );
    }
    return output;
}

BonsaiNchwTensor packed_latents_tensor(
    const std::vector<float>& packed_latents,
    const BonsaiLatentShape& latent_shape,
    uint64_t image_height,
    uint64_t image_width
) {
    return BonsaiNchwTensor {
        latent_shape.batch_size,
        latent_shape.channels,
        latent_shape.latent_height,
        latent_shape.latent_width,
        bonsai_unpack_packed_latents(
            packed_latents,
            latent_shape.batch_size,
            latent_shape.sequence_length,
            latent_shape.channels,
            image_height,
            image_width,
            8
        ),
    };
}

BonsaiTextPhaseOutput run_text_encoder_phase(
    const BonsaiGenerationRequest& effective_request,
    const BonsaiTokenizerData& tokenizer_data,
    const BonsaiQwenTextEncoderViews& text_encoder_views,
    const std::atomic_bool& cancel_requested
) {
    require_not_cancelled(cancel_requested);
    log_phase("prompt_plan_start");
    const BonsaiPromptEncodingPlan prompt_plan = bonsai_prepare_qwen_prompt_encoding_plan(
        effective_request.prompt,
        effective_request.negative_prompt,
        effective_request.cfg_scale,
        tokenizer_data.metadata
    );
    log_phase("qwen_input_start");
    BonsaiTextPhaseOutput output;
    output.input_shell = bonsai_prepare_qwen_input_shell(
        prompt_plan,
        text_encoder_views,
        tokenizer_data
    );
    require_not_cancelled(cancel_requested);
    log_phase("qwen_prompt_forward_start");
    output.prompt_embeddings = bonsai_qwen_text_encoder_forward(
        text_encoder_views,
        output.input_shell.prompt_input_ids,
        output.input_shell.prompt_attention_mask
    );
    log_phase("qwen_prompt_forward_done");
    if (output.input_shell.has_negative_prompt) {
        log_phase("qwen_negative_forward_start");
        output.negative_prompt_embeddings = bonsai_qwen_text_encoder_forward(
            text_encoder_views,
            output.input_shell.negative_input_ids,
            output.input_shell.negative_attention_mask
        );
        log_phase("qwen_negative_forward_done");
    }
    require_not_cancelled(cancel_requested);
    return output;
}

std::vector<float> run_denoise_phase(
    const BonsaiGenerationRequest& effective_request,
    int64_t seed,
    const BonsaiTextPhaseOutput& text_phase,
    const BonsaiFluxTransformerViews& transformer_views,
    const BonsaiProgressCallback& progress_callback,
    const std::atomic_bool& cancel_requested
) {
    log_phase("latents_start");
    const BonsaiLatentShape latent_shape = bonsai_packed_latent_shape(
        static_cast<uint64_t>(effective_request.height),
        static_cast<uint64_t>(effective_request.width),
        static_cast<uint64_t>(effective_request.batch_count),
        128,
        8
    );
    const BonsaiFlowMatchEulerSchedule schedule = bonsai_flow_match_euler_schedule(
        latent_shape.sequence_length,
        static_cast<uint64_t>(effective_request.sampling_steps)
    );
    std::vector<float> latents = bonsai_pack_latents_nchw(
        bonsai_random_latents_nchw(
            latent_shape.batch_size,
            latent_shape.channels,
            latent_shape.latent_height,
            latent_shape.latent_width,
            seed
        ),
        latent_shape.batch_size,
        latent_shape.channels,
        latent_shape.latent_height,
        latent_shape.latent_width
    );
    const std::vector<std::array<float, 4>> image_ids = float4_ids(
        bonsai_latent_grid_ids(
            latent_shape.batch_size,
            latent_shape.latent_height,
            latent_shape.latent_width
        ),
        "image"
    );
    const std::vector<std::array<float, 4>> text_ids = float4_ids(
        bonsai_qwen_text_ids(text_phase.prompt_embeddings.sequence_length),
        "text"
    );
    const std::vector<std::array<float, 4>> negative_text_ids =
        text_phase.input_shell.has_negative_prompt
            ? float4_ids(
                bonsai_qwen_text_ids(text_phase.negative_prompt_embeddings.sequence_length),
                "negative text"
            )
            : std::vector<std::array<float, 4>> {};

    if (progress_callback) {
        progress_callback(0, effective_request.sampling_steps);
    }
    require_not_cancelled(cancel_requested);

    log_phase("denoise_start");
    for (size_t index = 0; index < schedule.timesteps.size(); index++) {
        require_not_cancelled(cancel_requested);
        __android_log_print(
            ANDROID_LOG_INFO,
            LOG_TAG,
            "phase=denoise_step_start step=%zu/%zu",
            index + 1U,
            schedule.timesteps.size()
        );
        const BonsaiFluxTransformerOutput conditional = bonsai_flux_transformer_forward(
            transformer_views,
            latents,
            text_phase.prompt_embeddings.values,
            image_ids,
            text_ids,
            schedule.timesteps[index]
        );
        std::vector<float> noise = conditional.values;
        if (text_phase.input_shell.has_negative_prompt) {
            const BonsaiFluxTransformerOutput unconditional = bonsai_flux_transformer_forward(
                transformer_views,
                latents,
                text_phase.negative_prompt_embeddings.values,
                image_ids,
                negative_text_ids,
                schedule.timesteps[index]
            );
            noise = classifier_free_guidance(
                conditional.values,
                unconditional.values,
                effective_request.cfg_scale
            );
        }
        latents = bonsai_flow_match_euler_step(
            noise,
            static_cast<uint64_t>(index),
            latents,
            schedule
        );
        if (progress_callback) {
            progress_callback(static_cast<int32_t>(index + 1U), effective_request.sampling_steps);
        }
    }
    require_not_cancelled(cancel_requested);
    return latents;
}

std::string run_vae_phase(
    const BonsaiGenerationRequest& effective_request,
    const std::vector<float>& latents,
    const BonsaiVaeDecodeViews& vae_views
) {
    const BonsaiLatentShape latent_shape = bonsai_packed_latent_shape(
        static_cast<uint64_t>(effective_request.height),
        static_cast<uint64_t>(effective_request.width),
        static_cast<uint64_t>(effective_request.batch_count),
        128,
        8
    );
    log_phase("vae_decode_start");
    return bonsai_encode_nchw_tensor_as_png_base64(
        bonsai_vae_decode_packed_view_nchw(
            packed_latents_tensor(
                latents,
                latent_shape,
                static_cast<uint64_t>(effective_request.height),
                static_cast<uint64_t>(effective_request.width)
            ),
            vae_views
        )
    );
}

std::string bonsai_generate_image_performance(
    const BonsaiGenerationRequest& effective_request,
    int64_t seed,
    const BonsaiProgressCallback& progress_callback,
    const std::atomic_bool& cancel_requested,
    BonsaiMemoryPolicy policy
) {
    log_phase("load_context_start");
    std::unique_ptr<BonsaiRuntimeModelContext> model_context =
        bonsai_load_runtime_model_context(effective_request.model_paths);
    log_phase("load_context_done");
    log_memory_snapshot("full_context_loaded", policy);
    BonsaiTextPhaseOutput text_phase = run_text_encoder_phase(
        effective_request,
        model_context->tokenizer_data,
        model_context->text_encoder_views,
        cancel_requested
    );
    std::vector<float> latents = run_denoise_phase(
        effective_request,
        seed,
        text_phase,
        model_context->transformer_views,
        progress_callback,
        cancel_requested
    );
    return run_vae_phase(effective_request, latents, model_context->vae_views);
}

std::string bonsai_generate_image_staged(
    const BonsaiGenerationRequest& effective_request,
    int64_t seed,
    const BonsaiProgressCallback& progress_callback,
    const std::atomic_bool& cancel_requested,
    BonsaiMemoryPolicy policy
) {
    log_phase("load_text_context_start");
    std::unique_ptr<BonsaiTextEncoderRuntimeContext> text_context =
        bonsai_load_text_encoder_runtime_context(effective_request.model_paths);
    log_phase("load_text_context_done");
    log_memory_snapshot("text_context_loaded", policy);
    BonsaiTextPhaseOutput text_phase = run_text_encoder_phase(
        effective_request,
        text_context->tokenizer_data,
        text_context->text_encoder_views,
        cancel_requested
    );
    text_context.reset();
    purge_allocator("text_context_released", policy);

    log_phase("load_flux_context_start");
    std::unique_ptr<BonsaiFluxTransformerRuntimeContext> flux_context =
        bonsai_load_flux_transformer_runtime_context(effective_request.model_paths);
    log_phase("load_flux_context_done");
    log_memory_snapshot("flux_context_loaded", policy);
    std::vector<float> latents = run_denoise_phase(
        effective_request,
        seed,
        text_phase,
        flux_context->transformer_views,
        progress_callback,
        cancel_requested
    );
    flux_context.reset();
    purge_allocator("flux_context_released", policy);

    log_phase("load_vae_context_start");
    std::unique_ptr<BonsaiVaeRuntimeContext> vae_context =
        bonsai_load_vae_runtime_context(effective_request.model_paths);
    log_phase("load_vae_context_done");
    log_memory_snapshot("vae_context_loaded", policy);
    std::string output = run_vae_phase(effective_request, latents, vae_context->vae_views);
    vae_context.reset();
    purge_allocator("vae_context_released", policy);
    return output;
}

} // namespace

BonsaiGenerationCancelled::BonsaiGenerationCancelled() :
    std::runtime_error("Bonsai generation cancelled.") {}

std::string bonsai_generate_image(
    const BonsaiGenerationRequest& request,
    const BonsaiProgressCallback& progress_callback,
    const std::atomic_bool& cancel_requested
) {
    require_not_cancelled(cancel_requested);
    validate_request(request);
    const BonsaiVulkanBackendMode backend_mode = parse_backend_mode(request.backend);
    bonsai_vulkan_set_backend_mode(backend_mode);
    const uint64_t total_ram_kb = android_total_ram_kb();
    const BonsaiMemoryPolicy policy = auto_memory_policy(total_ram_kb);
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=memory_policy memory_policy=%s total_ram_mb=%llu",
        memory_policy_name(policy),
        static_cast<unsigned long long>(total_ram_kb / 1024ULL)
    );
    log_memory_snapshot("request_start", policy);
    if (backend_mode != BonsaiVulkanBackendMode::Cpu) {
        bonsai_vulkan_runtime_available();
    }
    log_phase("parse_seed");
    const int64_t seed = parse_seed(request.seed);
    if (policy == BonsaiMemoryPolicy::Performance) {
        return bonsai_generate_image_performance(
            request,
            seed,
            progress_callback,
            cancel_requested,
            policy
        );
    }
    return bonsai_generate_image_staged(
        request,
        seed,
        progress_callback,
        cancel_requested,
        policy
    );
}
