#pragma once

#include <cstdint>
#include <vector>

std::vector<float> bonsai_repeat_kv_heads(
    const std::vector<float>& input,
    uint64_t key_value_heads,
    uint64_t repeats,
    uint64_t length,
    uint64_t head_dimension
);

std::vector<float> bonsai_scaled_dot_product_attention(
    const std::vector<float>& queries,
    const std::vector<float>& keys,
    const std::vector<float>& values,
    const std::vector<float>& additive_mask,
    uint64_t heads,
    uint64_t length,
    uint64_t head_dimension,
    float scale
);
