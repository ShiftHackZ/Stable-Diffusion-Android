#include "bonsai_flux_time_embedding.h"

#include <cmath>
#include <stdexcept>

namespace {

constexpr float FLUX_TIMESTEP_THETA = 10000.0F;

} // namespace

BonsaiFluxTimestepEmbedding bonsai_flux_timestep_embedding(
    const std::vector<float>& timesteps,
    uint64_t dimensions
) {
    if (timesteps.empty()) {
        throw std::runtime_error("Bonsai Flux timesteps must not be empty.");
    }
    if (dimensions == 0 || dimensions % 2 != 0) {
        throw std::runtime_error("Bonsai Flux timestep dimensions must be positive and even.");
    }

    const uint64_t half = dimensions / 2;
    BonsaiFluxTimestepEmbedding output {
        static_cast<uint64_t>(timesteps.size()),
        dimensions,
        {},
    };
    output.values.reserve(timesteps.size() * static_cast<size_t>(dimensions));

    std::vector<float> frequencies;
    frequencies.reserve(static_cast<size_t>(half));
    for (uint64_t index = 0; index < half; index++) {
        frequencies.push_back(
            std::exp(-std::log(FLUX_TIMESTEP_THETA) * static_cast<float>(index) /
                static_cast<float>(half))
        );
    }

    for (float timestep : timesteps) {
        for (float frequency : frequencies) {
            output.values.push_back(std::cos(timestep * frequency));
        }
        for (float frequency : frequencies) {
            output.values.push_back(std::sin(timestep * frequency));
        }
    }

    return output;
}
