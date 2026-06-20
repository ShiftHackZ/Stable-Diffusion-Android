#pragma once

#include "bonsai_packed_weight.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <vector>

struct BonsaiDenseWeightViews {
    BonsaiTensorView weight;
    uint64_t leading_rows = 0;
    uint64_t input_values = 0;
};

BonsaiDenseWeightViews bonsai_require_dense_weight_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& key
);

float bonsai_dense_matvec_row(
    const BonsaiDenseWeightViews& views,
    const std::vector<float>& input,
    uint64_t row
);

std::vector<float> bonsai_dense_matvec(
    const BonsaiDenseWeightViews& views,
    const std::vector<float>& input
);

void bonsai_dense_matvec_into(
    const BonsaiDenseWeightViews& views,
    const float* input,
    float* output
);

float bonsai_quantized_matvec_row(
    const BonsaiPackedWeightViews& views,
    const std::vector<float>& input,
    uint64_t row
);

std::vector<float> bonsai_quantized_matvec(
    const BonsaiPackedWeightViews& views,
    const std::vector<float>& input
);

void bonsai_quantized_matvec_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output
);
