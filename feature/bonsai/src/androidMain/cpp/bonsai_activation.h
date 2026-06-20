#pragma once

#include <cstdint>
#include <vector>

float bonsai_silu(float value);

std::vector<float> bonsai_silu(
    const std::vector<float>& input
);

std::vector<float> bonsai_silu_times(
    const std::vector<float>& gate,
    const std::vector<float>& up
);

std::vector<float> bonsai_swiglu(
    const std::vector<float>& input
);

std::vector<float> bonsai_swiglu_last_dimension(
    const std::vector<float>& input,
    uint64_t last_dimension
);
