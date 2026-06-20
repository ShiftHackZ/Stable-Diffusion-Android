#include "bonsai_layer_norm.h"

#include <cmath>
#include <stdexcept>
#include <string>

namespace {

void require_optional_vector_size(
    const std::vector<float>* values,
    size_t expected,
    const char* label
) {
    if (values != nullptr && values->size() != expected) {
        throw std::runtime_error(std::string("Bonsai LayerNorm ") + label + " size mismatch.");
    }
}

} // namespace

std::vector<float> bonsai_layer_norm(
    const std::vector<float>& input,
    uint64_t last_dimension,
    float epsilon,
    const std::vector<float>* weight,
    const std::vector<float>* bias
) {
    if (last_dimension == 0) {
        throw std::runtime_error("Bonsai LayerNorm last dimension must be positive.");
    }
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai LayerNorm epsilon must be finite and positive.");
    }

    const size_t dimension = static_cast<size_t>(last_dimension);
    if (input.empty() || input.size() % dimension != 0) {
        throw std::runtime_error("Bonsai LayerNorm input shape mismatch.");
    }
    require_optional_vector_size(weight, dimension, "weight");
    require_optional_vector_size(bias, dimension, "bias");

    std::vector<float> output(input.size(), 0.0F);
    for (size_t offset = 0; offset < input.size(); offset += dimension) {
        double mean = 0.0;
        for (size_t index = 0; index < dimension; index++) {
            mean += static_cast<double>(input[offset + index]);
        }
        mean /= static_cast<double>(dimension);

        double variance = 0.0;
        for (size_t index = 0; index < dimension; index++) {
            const double centered = static_cast<double>(input[offset + index]) - mean;
            variance += centered * centered;
        }
        variance /= static_cast<double>(dimension);

        const float scale = 1.0F / std::sqrt(static_cast<float>(variance) + epsilon);
        for (size_t index = 0; index < dimension; index++) {
            float value = (input[offset + index] - static_cast<float>(mean)) * scale;
            if (weight != nullptr) {
                value *= (*weight)[index];
            }
            if (bias != nullptr) {
                value += (*bias)[index];
            }
            output[offset + index] = value;
        }
    }
    return output;
}
