#pragma once

#include <cstdint>
#include <string>
#include <unordered_map>
#include <vector>

struct BonsaiTokenizerMetadata {
    std::string original_tokenizer_class;
    std::string runtime_tokenizer_class;
    std::string model_type;
    std::string pad_token;
    std::string eos_token;
    int32_t pad_token_id = 0;
    int32_t eos_token_id = 0;
    uint64_t vocab_size = 0;
    uint64_t merge_count = 0;
    bool qwen_tokenizer_class_rewritten = false;
    bool merge_pairs_need_normalization = false;
};

struct BonsaiTokenizerMerge {
    std::string first;
    std::string second;
};

struct BonsaiTokenizerAddedToken {
    std::string content;
    int32_t id = 0;
    bool special = false;
};

struct BonsaiTokenizerData {
    BonsaiTokenizerMetadata metadata;
    std::unordered_map<std::string, int32_t> vocab;
    std::unordered_map<std::string, int32_t> added_token_ids;
    std::vector<BonsaiTokenizerMerge> merges;
    std::vector<BonsaiTokenizerAddedToken> added_tokens;
};

BonsaiTokenizerMetadata bonsai_load_tokenizer_metadata(const std::string& tokenizer_path);

BonsaiTokenizerData bonsai_load_tokenizer_data(const std::string& tokenizer_path);

uint64_t bonsai_tokenizer_metadata_checksum(const BonsaiTokenizerMetadata& metadata);

uint64_t bonsai_tokenizer_data_checksum(const BonsaiTokenizerData& data);

std::vector<int32_t> bonsai_tokenizer_encode(
    const BonsaiTokenizerData& data,
    const std::string& text
);
