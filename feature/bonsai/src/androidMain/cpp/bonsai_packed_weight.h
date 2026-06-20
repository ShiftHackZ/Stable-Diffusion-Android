#pragma once

#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>

struct BonsaiPackedWeightViews {
    bool packed = false;
    BonsaiTensorView weight;
    BonsaiTensorView scales;
    BonsaiTensorView biases;
    int bits = 0;
    int group_size = 0;
    uint64_t leading_rows = 0;
    uint64_t input_values = 0;
};

BonsaiPackedWeightViews bonsai_require_packed_weight_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor
);

uint64_t bonsai_packed_weight_byte_count(const BonsaiPackedWeightViews& views);
