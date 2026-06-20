#pragma once

#include "bonsai_matmul.h"
#include "bonsai_packed_weight.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <string>
#include <vector>

enum class BonsaiLinearWeightKind {
    Dense,
    Packed,
};

struct BonsaiLinearViews {
    BonsaiLinearWeightKind kind = BonsaiLinearWeightKind::Dense;
    BonsaiDenseWeightViews dense;
    BonsaiPackedWeightViews packed;
    BonsaiTensorView bias;
    bool has_bias = false;
    uint64_t output_rows = 0;
    uint64_t input_values = 0;
};

BonsaiLinearViews bonsai_require_dense_linear_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& weight_key,
    const std::string& bias_key
);

BonsaiLinearViews bonsai_require_packed_linear_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor,
    const std::string& bias_key
);

float bonsai_linear_row(
    const BonsaiLinearViews& views,
    const std::vector<float>& input,
    uint64_t row
);

std::vector<float> bonsai_linear(
    const BonsaiLinearViews& views,
    const std::vector<float>& input
);

std::vector<float> bonsai_linear_sequence(
    const BonsaiLinearViews& views,
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length
);

uint64_t bonsai_linear_byte_count(const BonsaiLinearViews& views);
