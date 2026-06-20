#pragma once

#include "bonsai_safetensors.h"

#include <cstdint>
#include <vector>

struct BonsaiFluxVaeConfig {
    uint64_t block_out_channels_count = 0;
    uint64_t layers_per_block = 0;
    uint64_t norm_num_groups = 0;
    float batch_norm_eps = 0.0F;
    std::vector<uint64_t> block_out_channels;
};

struct BonsaiFluxVaeInventorySummary {
    uint64_t up_block_count = 0;
    uint64_t resnet_block_count = 0;
    uint64_t attention_block_count = 0;
    uint64_t logical_tensor_count = 0;
};

BonsaiFluxVaeInventorySummary bonsai_require_flux_vae_tensors(
    const BonsaiSafetensorsIndex& index,
    const BonsaiFluxVaeConfig& config
);
