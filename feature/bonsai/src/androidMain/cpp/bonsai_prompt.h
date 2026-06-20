#pragma once

#include <cstdint>
#include <string>
#include <vector>

struct BonsaiTokenizerMetadata;

struct BonsaiQwenPromptSpec {
    uint64_t max_sequence_length = 512;
    int32_t pad_token_id = 151643;
    int32_t eos_token_id = 151645;
};

struct BonsaiPromptEncodingPlan {
    std::string formatted_prompt;
    std::string formatted_negative_prompt;
    uint64_t max_sequence_length = 0;
    int32_t pad_token_id = 0;
    int32_t eos_token_id = 0;
    bool uses_negative_prompt = false;
    std::vector<int32_t> text_ids;
    std::vector<int32_t> negative_text_ids;
};

BonsaiQwenPromptSpec bonsai_qwen_prompt_spec();

std::string bonsai_qwen_chat_formatted_prompt(const std::string& prompt);

std::vector<int32_t> bonsai_qwen_text_ids(uint64_t length);

BonsaiPromptEncodingPlan bonsai_prepare_qwen_prompt_encoding_plan(
    const std::string& prompt,
    const std::string& negative_prompt,
    float cfg_scale,
    const BonsaiTokenizerMetadata& tokenizer_metadata
);

uint64_t bonsai_prompt_encoding_plan_checksum(const BonsaiPromptEncodingPlan& plan);
