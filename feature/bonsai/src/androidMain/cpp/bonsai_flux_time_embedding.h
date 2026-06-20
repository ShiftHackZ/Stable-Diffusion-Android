#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFluxTimestepEmbedding {
    uint64_t timestep_count = 0;
    uint64_t dimensions = 0;
    std::vector<float> values;
};

BonsaiFluxTimestepEmbedding bonsai_flux_timestep_embedding(
    const std::vector<float>& timesteps,
    uint64_t dimensions
);
