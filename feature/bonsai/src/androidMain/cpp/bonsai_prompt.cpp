#include "bonsai_prompt.h"

#include "bonsai_tokenizer.h"

#include <cmath>
#include <limits>
#include <stdexcept>

BonsaiQwenPromptSpec bonsai_qwen_prompt_spec() {
    return BonsaiQwenPromptSpec {};
}

std::string bonsai_qwen_chat_formatted_prompt(const std::string& prompt) {
    return "<|im_start|>user\n" +
        prompt +
        "<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n";
}

std::vector<int32_t> bonsai_qwen_text_ids(uint64_t length) {
    if (length > static_cast<uint64_t>(std::numeric_limits<int32_t>::max())) {
        throw std::runtime_error("Bonsai Qwen text id length is too large.");
    }
    std::vector<int32_t> values;
    values.reserve(static_cast<size_t>(length * 4U));
    for (uint64_t index = 0; index < length; index++) {
        values.push_back(0);
        values.push_back(0);
        values.push_back(0);
        values.push_back(static_cast<int32_t>(index));
    }
    return values;
}

BonsaiPromptEncodingPlan bonsai_prepare_qwen_prompt_encoding_plan(
    const std::string& prompt,
    const std::string& negative_prompt,
    float cfg_scale,
    const BonsaiTokenizerMetadata& tokenizer_metadata
) {
    if (!std::isfinite(cfg_scale)) {
        throw std::runtime_error("Bonsai CFG scale must be finite.");
    }

    const BonsaiQwenPromptSpec spec = bonsai_qwen_prompt_spec();
    BonsaiPromptEncodingPlan plan;
    plan.formatted_prompt = bonsai_qwen_chat_formatted_prompt(prompt);
    plan.max_sequence_length = spec.max_sequence_length;
    plan.pad_token_id = tokenizer_metadata.pad_token_id != 0
        ? tokenizer_metadata.pad_token_id
        : spec.pad_token_id;
    plan.eos_token_id = tokenizer_metadata.eos_token_id != 0
        ? tokenizer_metadata.eos_token_id
        : spec.eos_token_id;
    plan.uses_negative_prompt = cfg_scale > 1.0F;
    plan.text_ids = bonsai_qwen_text_ids(plan.max_sequence_length);

    if (plan.uses_negative_prompt) {
        const std::string effective_negative_prompt = negative_prompt.empty()
            ? " "
            : negative_prompt;
        plan.formatted_negative_prompt = bonsai_qwen_chat_formatted_prompt(
            effective_negative_prompt
        );
        plan.negative_text_ids = bonsai_qwen_text_ids(plan.max_sequence_length);
    }

    return plan;
}

uint64_t bonsai_prompt_encoding_plan_checksum(const BonsaiPromptEncodingPlan& plan) {
    uint64_t checksum = plan.max_sequence_length * 3U;
    checksum += static_cast<uint64_t>(plan.pad_token_id) * 5U;
    checksum += static_cast<uint64_t>(plan.eos_token_id) * 7U;
    checksum += plan.uses_negative_prompt ? 11U : 0U;
    checksum += static_cast<uint64_t>(plan.formatted_prompt.size()) * 13U;
    checksum += static_cast<uint64_t>(plan.formatted_negative_prompt.size()) * 17U;
    checksum += static_cast<uint64_t>(plan.text_ids.size()) * 19U;
    checksum += static_cast<uint64_t>(plan.negative_text_ids.size()) * 23U;
    for (char value : plan.formatted_prompt) {
        checksum += static_cast<uint64_t>(static_cast<unsigned char>(value));
    }
    for (char value : plan.formatted_negative_prompt) {
        checksum += static_cast<uint64_t>(static_cast<unsigned char>(value)) * 2U;
    }
    return checksum;
}
