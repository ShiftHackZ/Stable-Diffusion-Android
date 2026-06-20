#include "bonsai_norm.h"

#include "bonsai_tensor.h"

#include <cmath>
#include <stdexcept>

BonsaiRmsNormWeightViews bonsai_require_rms_norm_weight(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& weight_key
) {
    BonsaiTensorView weight = storage.require_view(index, weight_key);
    if (!bonsai_dtype_is_floating_point(weight.dtype)) {
        throw std::runtime_error("Bonsai RMSNorm weight must be floating point: " + weight_key);
    }
    if (weight.element_count == 0) {
        throw std::runtime_error("Bonsai RMSNorm weight must not be empty: " + weight_key);
    }
    return BonsaiRmsNormWeightViews {
        weight,
        weight.element_count,
    };
}

std::vector<float> bonsai_rms_norm(
    const std::vector<float>& input,
    const BonsaiRmsNormWeightViews& views,
    float eps
) {
    if (views.dimensions == 0 || input.size() % static_cast<size_t>(views.dimensions) != 0) {
        throw std::runtime_error(
            "Bonsai RMSNorm input size mismatch: " + views.weight.descriptor->key
        );
    }

    std::vector<float> output;
    output.reserve(input.size());
    for (size_t offset = 0; offset < input.size(); offset += static_cast<size_t>(views.dimensions)) {
        double squared_sum = 0.0;
        for (uint64_t index = 0; index < views.dimensions; index++) {
            const float value = input[offset + static_cast<size_t>(index)];
            squared_sum += static_cast<double>(value) * static_cast<double>(value);
        }

        const float scale = 1.0F / std::sqrt(
            static_cast<float>(squared_sum / static_cast<double>(views.dimensions)) + eps
        );
        for (uint64_t index = 0; index < views.dimensions; index++) {
            const float weight = bonsai_read_scalar_as_f32(
                views.weight.data + index * views.weight.dtype_byte_count,
                views.weight.dtype
            );
            output.push_back(input[offset + static_cast<size_t>(index)] * scale * weight);
        }
    }
    return output;
}

uint64_t bonsai_rms_norm_byte_count(const BonsaiRmsNormWeightViews& views) {
    return views.weight.byte_count;
}
