#pragma once

#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <string>
#include <vector>

struct BonsaiRmsNormWeightViews {
    BonsaiTensorView weight;
    uint64_t dimensions = 0;
};

BonsaiRmsNormWeightViews bonsai_require_rms_norm_weight(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& weight_key
);

std::vector<float> bonsai_rms_norm(
    const std::vector<float>& input,
    const BonsaiRmsNormWeightViews& views,
    float eps
);

uint64_t bonsai_rms_norm_byte_count(const BonsaiRmsNormWeightViews& views);
