#pragma once

#include <array>
#include <cstdint>
#include <vector>

struct BonsaiFluxRotaryEmbedding {
    uint64_t token_count = 0;
    uint64_t dimensions = 0;
    std::vector<float> cos;
    std::vector<float> sin;
};

BonsaiFluxRotaryEmbedding bonsai_flux_pos_embed(
    const std::vector<std::array<float, 4>>& ids
);
