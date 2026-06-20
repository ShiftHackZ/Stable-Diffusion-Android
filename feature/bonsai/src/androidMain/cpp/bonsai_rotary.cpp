#include "bonsai_rotary.h"

#include <cmath>
#include <stdexcept>

namespace {

void require_even_head_dimension(uint64_t head_dimension) {
    if (head_dimension == 0 || head_dimension % 2 != 0) {
        throw std::runtime_error("Bonsai rotary head dimension must be positive and even.");
    }
}

} // namespace

std::vector<float> bonsai_rotary_inv_frequencies(
    uint64_t head_dimension,
    float base
) {
    require_even_head_dimension(head_dimension);

    std::vector<float> output;
    output.reserve(static_cast<size_t>(head_dimension / 2));
    for (uint64_t index = 0; index < head_dimension; index += 2) {
        output.push_back(1.0F / std::pow(base, static_cast<float>(index) / head_dimension));
    }
    return output;
}

std::vector<float> bonsai_rotary_cos_values(
    uint64_t position,
    uint64_t head_dimension,
    float base
) {
    const std::vector<float> inv_frequencies = bonsai_rotary_inv_frequencies(
        head_dimension,
        base
    );

    std::vector<float> output;
    output.reserve(static_cast<size_t>(head_dimension));
    for (float frequency : inv_frequencies) {
        output.push_back(std::cos(static_cast<float>(position) * frequency));
    }
    for (float frequency : inv_frequencies) {
        output.push_back(std::cos(static_cast<float>(position) * frequency));
    }
    return output;
}

std::vector<float> bonsai_rotary_sin_values(
    uint64_t position,
    uint64_t head_dimension,
    float base
) {
    const std::vector<float> inv_frequencies = bonsai_rotary_inv_frequencies(
        head_dimension,
        base
    );

    std::vector<float> output;
    output.reserve(static_cast<size_t>(head_dimension));
    for (float frequency : inv_frequencies) {
        output.push_back(std::sin(static_cast<float>(position) * frequency));
    }
    for (float frequency : inv_frequencies) {
        output.push_back(std::sin(static_cast<float>(position) * frequency));
    }
    return output;
}

std::vector<float> bonsai_rotate_half(
    const std::vector<float>& input,
    uint64_t head_dimension
) {
    require_even_head_dimension(head_dimension);
    if (input.size() % static_cast<size_t>(head_dimension) != 0) {
        throw std::runtime_error("Bonsai rotate-half input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(input.size());
    const size_t dimension = static_cast<size_t>(head_dimension);
    const size_t half = dimension / 2;
    for (size_t offset = 0; offset < input.size(); offset += dimension) {
        for (size_t index = 0; index < half; index++) {
            output.push_back(-input[offset + half + index]);
        }
        for (size_t index = 0; index < half; index++) {
            output.push_back(input[offset + index]);
        }
    }
    return output;
}

std::vector<float> bonsai_apply_rotary_to_heads(
    const std::vector<float>& input,
    uint64_t head_dimension,
    uint64_t position,
    float base
) {
    require_even_head_dimension(head_dimension);
    if (input.size() % static_cast<size_t>(head_dimension) != 0) {
        throw std::runtime_error("Bonsai rotary input size mismatch.");
    }

    const std::vector<float> rotated = bonsai_rotate_half(input, head_dimension);
    const std::vector<float> cos_values = bonsai_rotary_cos_values(
        position,
        head_dimension,
        base
    );
    const std::vector<float> sin_values = bonsai_rotary_sin_values(
        position,
        head_dimension,
        base
    );

    std::vector<float> output;
    output.reserve(input.size());
    const size_t dimension = static_cast<size_t>(head_dimension);
    for (size_t index = 0; index < input.size(); index++) {
        const size_t column = index % dimension;
        output.push_back(input[index] * cos_values[column] + rotated[index] * sin_values[column]);
    }
    return output;
}
