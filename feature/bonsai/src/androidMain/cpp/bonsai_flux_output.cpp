#include "bonsai_flux_output.h"

#include "bonsai_flux_modulation.h"

#include <cmath>
#include <limits>
#include <stdexcept>
#include <string>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai Flux output shape overflow: ") + label);
    }
    return left * right;
}

void require_positive(uint64_t value, const char* label) {
    if (value == 0) {
        throw std::runtime_error(std::string("Bonsai Flux output value must be positive: ") + label);
    }
}

} // namespace

std::vector<float> bonsai_flux_final_projection_input(
    const std::vector<float>& image_tokens,
    const std::vector<float>& norm_out_modulation_values,
    uint64_t batch,
    uint64_t image_sequence_length,
    uint64_t dimensions,
    float layer_norm_epsilon
) {
    require_positive(batch, "batch");
    require_positive(image_sequence_length, "image sequence");
    require_positive(dimensions, "dimensions");
    if (layer_norm_epsilon <= 0.0F || !std::isfinite(layer_norm_epsilon)) {
        throw std::runtime_error("Bonsai Flux output LayerNorm epsilon must be finite and positive.");
    }

    const uint64_t expected = checked_multiply(
        checked_multiply(batch, image_sequence_length, "image tokens"),
        dimensions,
        "image tokens"
    );
    if (image_tokens.size() != static_cast<size_t>(expected)) {
        throw std::runtime_error("Bonsai Flux output image token shape mismatch.");
    }

    const BonsaiFluxNormOutModulation modulation = bonsai_flux_split_norm_out_modulation(
        norm_out_modulation_values,
        batch,
        dimensions
    );
    return bonsai_flux_apply_modulated_layer_norm(
        image_tokens,
        modulation.shift,
        modulation.scale,
        batch,
        image_sequence_length,
        dimensions,
        layer_norm_epsilon
    );
}
