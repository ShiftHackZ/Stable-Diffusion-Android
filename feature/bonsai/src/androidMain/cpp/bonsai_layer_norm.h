#pragma once

#include <cstdint>
#include <vector>

std::vector<float> bonsai_layer_norm(
    const std::vector<float>& input,
    uint64_t last_dimension,
    float epsilon,
    const std::vector<float>* weight,
    const std::vector<float>* bias
);
