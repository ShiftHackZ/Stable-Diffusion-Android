#include "bonsai_flux_pos_embed.h"

#include <cmath>
#include <stdexcept>

namespace {

constexpr float FLUX_POS_THETA = 2000.0F;
constexpr uint64_t FLUX_AXIS_COUNT = 4;
constexpr uint64_t FLUX_AXIS_DIMENSION = 32;
constexpr uint64_t FLUX_AXIS_HALF_DIMENSION = FLUX_AXIS_DIMENSION / 2;
constexpr uint64_t FLUX_ROTARY_DIMENSION = FLUX_AXIS_COUNT * FLUX_AXIS_HALF_DIMENSION;

float flux_axis_omega(uint64_t index) {
    const float scale = static_cast<float>(index * 2U) / static_cast<float>(FLUX_AXIS_DIMENSION);
    return 1.0F / std::pow(FLUX_POS_THETA, scale);
}

} // namespace

BonsaiFluxRotaryEmbedding bonsai_flux_pos_embed(
    const std::vector<std::array<float, 4>>& ids
) {
    if (ids.empty()) {
        throw std::runtime_error("Bonsai Flux position ids must not be empty.");
    }

    BonsaiFluxRotaryEmbedding output {
        static_cast<uint64_t>(ids.size()),
        FLUX_ROTARY_DIMENSION,
        {},
        {},
    };
    output.cos.reserve(ids.size() * static_cast<size_t>(FLUX_ROTARY_DIMENSION));
    output.sin.reserve(ids.size() * static_cast<size_t>(FLUX_ROTARY_DIMENSION));

    for (const std::array<float, 4>& id : ids) {
        for (uint64_t axis = 0; axis < FLUX_AXIS_COUNT; axis++) {
            for (uint64_t index = 0; index < FLUX_AXIS_HALF_DIMENSION; index++) {
                const float value = id[static_cast<size_t>(axis)] * flux_axis_omega(index);
                output.cos.push_back(std::cos(value));
                output.sin.push_back(std::sin(value));
            }
        }
    }

    return output;
}
