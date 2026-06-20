#pragma once

#include "bonsai_prompt.h"
#include "bonsai_qwen.h"
#include "bonsai_tokenizer.h"

#include <cstdint>
#include <vector>

struct BonsaiQwenInputShell {
    uint64_t batch = 1;
    uint64_t sequence_length = 0;
    uint64_t hidden_size = 0;
    bool tokenization_pending = true;
    bool has_negative_prompt = false;
    uint64_t prompt_token_count = 0;
    uint64_t negative_token_count = 0;
    std::vector<int32_t> prompt_input_ids;
    std::vector<int32_t> negative_input_ids;
    std::vector<int32_t> prompt_attention_mask;
    std::vector<int32_t> negative_attention_mask;
};

BonsaiQwenInputShell bonsai_prepare_qwen_input_shell(
    const BonsaiPromptEncodingPlan& prompt_plan,
    const BonsaiQwenTextEncoderViews& text_encoder_views,
    const BonsaiTokenizerData& tokenizer_data
);

uint64_t bonsai_qwen_input_shell_checksum(const BonsaiQwenInputShell& input);
