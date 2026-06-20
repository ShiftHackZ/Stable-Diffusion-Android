#include "bonsai_activation.h"

#include <cmath>
#include <stdexcept>

float bonsai_silu(float value) {
    return value / (1.0F + std::exp(-value));
}

std::vector<float> bonsai_silu(
    const std::vector<float>& input
) {
    std::vector<float> output;
    output.reserve(input.size());
    for (float value : input) {
        output.push_back(bonsai_silu(value));
    }
    return output;
}

std::vector<float> bonsai_silu_times(
    const std::vector<float>& gate,
    const std::vector<float>& up
) {
    if (gate.size() != up.size()) {
        throw std::runtime_error("Bonsai SiLU gate/up size mismatch.");
    }

    std::vector<float> output;
    output.reserve(gate.size());
    for (size_t index = 0; index < gate.size(); index++) {
        output.push_back(bonsai_silu(gate[index]) * up[index]);
    }
    return output;
}

std::vector<float> bonsai_swiglu(
    const std::vector<float>& input
) {
    if (input.empty() || input.size() % 2 != 0) {
        throw std::runtime_error("Bonsai SwiGLU input size must be positive and even.");
    }

    const size_t half = input.size() / 2;
    std::vector<float> output;
    output.reserve(half);
    for (size_t index = 0; index < half; index++) {
        output.push_back(bonsai_silu(input[index]) * input[half + index]);
    }
    return output;
}

std::vector<float> bonsai_swiglu_last_dimension(
    const std::vector<float>& input,
    uint64_t last_dimension
) {
    if (last_dimension == 0 || last_dimension % 2 != 0) {
        throw std::runtime_error("Bonsai SwiGLU last dimension must be positive and even.");
    }
    const size_t dimension = static_cast<size_t>(last_dimension);
    if (input.empty() || input.size() % dimension != 0) {
        throw std::runtime_error("Bonsai SwiGLU input shape mismatch.");
    }

    std::vector<float> output;
    output.reserve(input.size() / 2);
    const size_t half = dimension / 2;
    for (size_t offset = 0; offset < input.size(); offset += dimension) {
        for (size_t index = 0; index < half; index++) {
            output.push_back(bonsai_silu(input[offset + index]) * input[offset + half + index]);
        }
    }
    return output;
}
