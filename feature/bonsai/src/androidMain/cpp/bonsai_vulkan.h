#pragma once

#include "bonsai_packed_weight.h"

#include <cstdint>

enum class BonsaiVulkanBackendMode {
    Auto,
    Cpu,
    Vulkan,
};

void bonsai_vulkan_set_backend_mode(BonsaiVulkanBackendMode mode);

bool bonsai_vulkan_runtime_available();

bool bonsai_vulkan_quantized_matvec_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output
);

bool bonsai_vulkan_quantized_matvec_sequence_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output,
    uint64_t token_count
);
