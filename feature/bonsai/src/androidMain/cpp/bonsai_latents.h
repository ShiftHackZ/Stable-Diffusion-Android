#pragma once

#include <cstdint>
#include <string>
#include <vector>

struct BonsaiLatentShape {
    uint64_t batch_size = 0;
    uint64_t channels = 0;
    uint64_t latent_height = 0;
    uint64_t latent_width = 0;
    uint64_t sequence_length = 0;
};

BonsaiLatentShape bonsai_packed_latent_shape(
    uint64_t image_height,
    uint64_t image_width,
    uint64_t batch_size,
    uint64_t channels,
    uint64_t vae_scale_factor
);

std::vector<int32_t> bonsai_latent_grid_ids(
    uint64_t batch_size,
    uint64_t latent_height,
    uint64_t latent_width
);

std::vector<float> bonsai_random_latents_nchw(
    uint64_t batch_size,
    uint64_t channels,
    uint64_t latent_height,
    uint64_t latent_width,
    int64_t seed
);

std::vector<float> bonsai_pack_latents_nchw(
    const std::vector<float>& latents,
    uint64_t batch_size,
    uint64_t channels,
    uint64_t latent_height,
    uint64_t latent_width
);

std::vector<float> bonsai_unpack_packed_latents(
    const std::vector<float>& latents,
    uint64_t batch_size,
    uint64_t sequence_length,
    uint64_t channels,
    uint64_t image_height,
    uint64_t image_width,
    uint64_t vae_scale_factor
);
