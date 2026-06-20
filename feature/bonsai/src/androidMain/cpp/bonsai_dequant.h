#pragma once

#include "bonsai_packed_weight.h"

#include <cstdint>
#include <vector>

uint32_t bonsai_unpack_quantized_value(
    uint32_t word,
    int bits,
    uint64_t packed_value_index
);

std::vector<float> bonsai_dequantize_packed_row(
    const BonsaiPackedWeightViews& views,
    uint64_t row
);

float bonsai_dequantize_packed_value(
    const BonsaiPackedWeightViews& views,
    uint64_t row,
    uint64_t column
);
