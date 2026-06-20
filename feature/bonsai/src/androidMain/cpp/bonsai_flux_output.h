#pragma once

#include <cstdint>
#include <vector>

std::vector<float> bonsai_flux_final_projection_input(
    const std::vector<float>& image_tokens,
    const std::vector<float>& norm_out_modulation_values,
    uint64_t batch,
    uint64_t image_sequence_length,
    uint64_t dimensions,
    float layer_norm_epsilon
);
