#pragma once

#include <cstdint>
#include <vector>

std::vector<float> bonsai_rotary_inv_frequencies(
    uint64_t head_dimension,
    float base
);

std::vector<float> bonsai_rotary_cos_values(
    uint64_t position,
    uint64_t head_dimension,
    float base
);

std::vector<float> bonsai_rotary_sin_values(
    uint64_t position,
    uint64_t head_dimension,
    float base
);

std::vector<float> bonsai_rotate_half(
    const std::vector<float>& input,
    uint64_t head_dimension
);

std::vector<float> bonsai_apply_rotary_to_heads(
    const std::vector<float>& input,
    uint64_t head_dimension,
    uint64_t position,
    float base
);
