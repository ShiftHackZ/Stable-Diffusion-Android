#pragma once

#include <cstdint>
#include <vector>

std::vector<float> bonsai_flux_apply_rms_norm_and_rope(
    const std::vector<float>& input,
    const std::vector<float>& norm_weight,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t heads,
    uint64_t sequence_length,
    uint64_t head_dimension,
    float epsilon
);
