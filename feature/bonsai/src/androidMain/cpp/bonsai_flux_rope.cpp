#include "bonsai_flux_rope.h"

#include <cmath>
#include <stdexcept>

namespace {

uint64_t attention_size(uint64_t heads, uint64_t sequence_length, uint64_t head_dimension) {
    return heads * sequence_length * head_dimension;
}

} // namespace

std::vector<float> bonsai_flux_apply_rms_norm_and_rope(
    const std::vector<float>& input,
    const std::vector<float>& norm_weight,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t heads,
    uint64_t sequence_length,
    uint64_t head_dimension,
    float epsilon
) {
    if (heads == 0 || sequence_length == 0 || head_dimension == 0 || head_dimension % 2 != 0) {
        throw std::runtime_error("Bonsai Flux RoPE dimensions must be positive and even.");
    }
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai Flux RoPE epsilon must be finite and positive.");
    }

    const uint64_t expected_input = attention_size(heads, sequence_length, head_dimension);
    const uint64_t rotary_dimension = head_dimension / 2;
    const uint64_t expected_rotary = sequence_length * rotary_dimension;
    if (input.size() != static_cast<size_t>(expected_input) ||
        norm_weight.size() != static_cast<size_t>(head_dimension) ||
        cos_values.size() != static_cast<size_t>(expected_rotary) ||
        sin_values.size() != static_cast<size_t>(expected_rotary)) {
        throw std::runtime_error("Bonsai Flux RoPE input shape mismatch.");
    }

    std::vector<float> output(input.size(), 0.0F);
    for (uint64_t head = 0; head < heads; head++) {
        for (uint64_t position = 0; position < sequence_length; position++) {
            const uint64_t offset = (head * sequence_length + position) * head_dimension;
            double mean_square = 0.0;
            for (uint64_t index = 0; index < head_dimension; index++) {
                const float value = input[static_cast<size_t>(offset + index)];
                mean_square += static_cast<double>(value) * static_cast<double>(value);
            }
            mean_square /= static_cast<double>(head_dimension);
            const float scale = 1.0F / std::sqrt(static_cast<float>(mean_square) + epsilon);

            for (uint64_t pair = 0; pair < rotary_dimension; pair++) {
                const size_t real_index = static_cast<size_t>(offset + pair * 2);
                const size_t imag_index = static_cast<size_t>(real_index + 1);
                const size_t rotary_index = static_cast<size_t>(
                    position * rotary_dimension + pair
                );

                const float real = input[real_index] * scale * norm_weight[static_cast<size_t>(pair * 2)];
                const float imag = input[imag_index] * scale *
                    norm_weight[static_cast<size_t>(pair * 2 + 1)];
                const float cos_value = cos_values[rotary_index];
                const float sin_value = sin_values[rotary_index];
                output[real_index] = real * cos_value - imag * sin_value;
                output[imag_index] = imag * cos_value + real * sin_value;
            }
        }
    }
    return output;
}
