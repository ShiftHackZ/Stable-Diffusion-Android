#include "bonsai_qwen_inputs.h"

#include <android/log.h>

#include <algorithm>
#include <cstddef>
#include <stdexcept>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";

void log_input_phase(const char* phase, uint64_t value = 0) {
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=%s value=%llu",
        phase,
        static_cast<unsigned long long>(value)
    );
}

void require_text_ids_shape(
    const std::vector<int32_t>& text_ids,
    uint64_t sequence_length,
    const std::string& label
) {
    if (text_ids.size() != static_cast<size_t>(sequence_length * 4U)) {
        throw std::runtime_error("Bonsai Qwen " + label + " text id shape mismatch.");
    }
}

std::vector<int32_t> limited_ids(
    const std::vector<int32_t>& ids,
    uint64_t max_sequence_length,
    int32_t fallback_token_id
) {
    std::vector<int32_t> output;
    output.reserve(static_cast<size_t>(max_sequence_length));
    const size_t token_limit = std::min(ids.size(), static_cast<size_t>(max_sequence_length));
    output.insert(output.end(), ids.begin(), ids.begin() + static_cast<std::ptrdiff_t>(token_limit));
    if (output.empty()) {
        output.push_back(fallback_token_id);
    }
    return output;
}

std::vector<int32_t> attention_mask(size_t sequence_length) {
    return std::vector<int32_t>(sequence_length, 1);
}

} // namespace

BonsaiQwenInputShell bonsai_prepare_qwen_input_shell(
    const BonsaiPromptEncodingPlan& prompt_plan,
    const BonsaiQwenTextEncoderViews& text_encoder_views,
    const BonsaiTokenizerData& tokenizer_data
) {
    if (prompt_plan.max_sequence_length == 0) {
        throw std::runtime_error("Bonsai Qwen prompt sequence length must be positive.");
    }
    if (text_encoder_views.hidden_size == 0 ||
        text_encoder_views.layers.empty() ||
        text_encoder_views.embedding.dimensions != text_encoder_views.hidden_size ||
        text_encoder_views.final_norm.dimensions != text_encoder_views.hidden_size) {
        throw std::runtime_error("Bonsai Qwen text encoder views are incomplete.");
    }

    require_text_ids_shape(
        prompt_plan.text_ids,
        prompt_plan.max_sequence_length,
        "prompt"
    );
    if (prompt_plan.uses_negative_prompt) {
        require_text_ids_shape(
            prompt_plan.negative_text_ids,
            prompt_plan.max_sequence_length,
            "negative prompt"
        );
    }

    BonsaiQwenInputShell input;
    input.hidden_size = text_encoder_views.hidden_size;
    input.tokenization_pending = false;
    input.has_negative_prompt = prompt_plan.uses_negative_prompt;
    log_input_phase("qwen_prompt_tokenize_start", prompt_plan.formatted_prompt.size());
    const std::vector<int32_t> prompt_ids = bonsai_tokenizer_encode(
        tokenizer_data,
        prompt_plan.formatted_prompt
    );
    log_input_phase("qwen_prompt_tokenize_done", prompt_ids.size());
    input.prompt_input_ids = limited_ids(
        prompt_ids,
        prompt_plan.max_sequence_length,
        prompt_plan.eos_token_id
    );
    input.prompt_token_count = static_cast<uint64_t>(input.prompt_input_ids.size());
    input.sequence_length = input.prompt_token_count;
    input.prompt_attention_mask = attention_mask(input.prompt_input_ids.size());
    log_input_phase("qwen_prompt_sequence_ready", input.prompt_token_count);
    if (input.has_negative_prompt) {
        log_input_phase(
            "qwen_negative_tokenize_start",
            prompt_plan.formatted_negative_prompt.size()
        );
        const std::vector<int32_t> negative_ids = bonsai_tokenizer_encode(
            tokenizer_data,
            prompt_plan.formatted_negative_prompt
        );
        log_input_phase("qwen_negative_tokenize_done", negative_ids.size());
        input.negative_input_ids = limited_ids(
            negative_ids,
            prompt_plan.max_sequence_length,
            prompt_plan.eos_token_id
        );
        input.negative_token_count = static_cast<uint64_t>(input.negative_input_ids.size());
        input.sequence_length = std::max(input.sequence_length, input.negative_token_count);
        input.negative_attention_mask = attention_mask(input.negative_input_ids.size());
        log_input_phase("qwen_negative_sequence_ready", input.negative_token_count);
    }
    return input;
}

uint64_t bonsai_qwen_input_shell_checksum(const BonsaiQwenInputShell& input) {
    uint64_t checksum = input.batch * 3U;
    checksum += input.sequence_length * 5U;
    checksum += input.hidden_size * 7U;
    checksum += input.tokenization_pending ? 11U : 0U;
    checksum += input.has_negative_prompt ? 13U : 0U;
    checksum += input.prompt_token_count * 17U;
    checksum += input.negative_token_count * 19U;
    checksum += static_cast<uint64_t>(input.prompt_input_ids.size()) * 23U;
    checksum += static_cast<uint64_t>(input.negative_input_ids.size()) * 29U;
    checksum += static_cast<uint64_t>(input.prompt_attention_mask.size()) * 31U;
    checksum += static_cast<uint64_t>(input.negative_attention_mask.size()) * 37U;
    const size_t prompt_limit = std::min<size_t>(input.prompt_input_ids.size(), 16);
    for (size_t index = 0; index < prompt_limit; index++) {
        checksum += static_cast<uint64_t>(input.prompt_input_ids[index]) * (index + 1U);
    }
    return checksum;
}
