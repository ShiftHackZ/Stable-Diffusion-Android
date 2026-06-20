#include "bonsai_packed_weight.h"

#include <stdexcept>
#include <string>

namespace {

void require_supported_bits(int bits) {
    if (bits != 1 && bits != 2 && bits != 4) {
        throw std::runtime_error("Unsupported Bonsai quantization bits: " + std::to_string(bits));
    }
}

uint64_t checked_multiply(uint64_t left, uint64_t right, const std::string& tensor_key) {
    if (left != 0 && right > UINT64_MAX / left) {
        throw std::runtime_error("Bonsai packed weight shape is too large: " + tensor_key);
    }
    return left * right;
}

uint64_t leading_row_count(const BonsaiTensorView& view) {
    if (view.descriptor->shape.empty()) {
        throw std::runtime_error("Bonsai packed weight must have at least one dimension.");
    }

    uint64_t rows = 1;
    for (size_t index = 0; index + 1 < view.descriptor->shape.size(); index++) {
        rows = checked_multiply(rows, view.descriptor->shape[index], view.descriptor->key);
    }
    return rows;
}

uint64_t last_dimension(const BonsaiTensorView& view) {
    if (view.descriptor->shape.empty()) {
        throw std::runtime_error("Bonsai packed weight must have at least one dimension.");
    }
    return view.descriptor->shape.back();
}

void require_same_shape(
    const BonsaiTensorView& left,
    const BonsaiTensorView& right,
    const std::string& label
) {
    if (left.descriptor->shape != right.descriptor->shape) {
        throw std::runtime_error(
            "Bonsai packed weight " +
            label +
            " shape mismatch: " +
            left.descriptor->key +
            " vs " +
            right.descriptor->key
        );
    }
}

void require_same_leading_shape(
    const BonsaiTensorView& packed,
    const BonsaiTensorView& scales
) {
    const auto& packed_shape = packed.descriptor->shape;
    const auto& scales_shape = scales.descriptor->shape;
    if (packed_shape.size() != scales_shape.size()) {
        throw std::runtime_error(
            "Bonsai packed weight rank mismatch: " + packed.descriptor->key
        );
    }
    for (size_t index = 0; index + 1 < packed_shape.size(); index++) {
        if (packed_shape[index] != scales_shape[index]) {
            throw std::runtime_error(
                "Bonsai packed weight leading shape mismatch: " + packed.descriptor->key
            );
        }
    }
}

uint64_t expected_packed_last_dimension(
    uint64_t scale_groups,
    int bits,
    int group_size,
    const std::string& tensor_key
) {
    const uint64_t values_per_word = 32ULL / static_cast<uint64_t>(bits);
    const uint64_t values = checked_multiply(
        scale_groups,
        static_cast<uint64_t>(group_size),
        tensor_key
    );
    if (values % values_per_word != 0) {
        throw std::runtime_error(
            "Bonsai packed weight group shape is not aligned to uint32 packing: " + tensor_key
        );
    }
    return values / values_per_word;
}

void validate_packed_views(const BonsaiPackedWeightViews& views) {
    require_supported_bits(views.bits);
    if (views.group_size <= 0) {
        throw std::runtime_error("Bonsai packed weight group size must be positive.");
    }
    if (32 % views.bits != 0) {
        throw std::runtime_error("Bonsai packed weight bits must divide 32.");
    }
    if (views.weight.dtype != BonsaiDType::U32) {
        throw std::runtime_error(
            "packed tensor " + views.weight.descriptor->key + " must be uint32"
        );
    }
    if (!bonsai_dtype_is_floating_point(views.scales.dtype) ||
        !bonsai_dtype_is_floating_point(views.biases.dtype)
    ) {
        throw std::runtime_error(
            "Bonsai packed weight scales and biases must be floating point: " +
            views.weight.descriptor->key
        );
    }

    require_same_shape(views.scales, views.biases, "scale/bias");
    require_same_leading_shape(views.weight, views.scales);
    const uint64_t expected_last = expected_packed_last_dimension(
        last_dimension(views.scales),
        views.bits,
        views.group_size,
        views.weight.descriptor->key
    );
    if (last_dimension(views.weight) != expected_last) {
        throw std::runtime_error(
            "Bonsai packed weight data shape mismatch: " + views.weight.descriptor->key
        );
    }
}

} // namespace

BonsaiPackedWeightViews bonsai_require_packed_weight_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor
) {
    BonsaiPackedWeightViews views {
        descriptor.packed,
        storage.require_view(index, descriptor.weight_key),
        {},
        {},
        descriptor.bits,
        descriptor.group_size,
        0,
        0,
    };

    views.leading_rows = leading_row_count(views.weight);
    if (!descriptor.packed) {
        views.input_values = last_dimension(views.weight);
        return views;
    }

    views.scales = storage.require_view(index, descriptor.scales_key);
    views.biases = storage.require_view(index, descriptor.biases_key);
    validate_packed_views(views);
    views.leading_rows = leading_row_count(views.weight);
    views.input_values = checked_multiply(
        last_dimension(views.scales),
        static_cast<uint64_t>(descriptor.group_size),
        views.weight.descriptor->key
    );
    return views;
}

uint64_t bonsai_packed_weight_byte_count(const BonsaiPackedWeightViews& views) {
    uint64_t bytes = views.weight.byte_count;
    if (views.packed) {
        bytes += views.scales.byte_count;
        bytes += views.biases.byte_count;
    }
    return bytes;
}
