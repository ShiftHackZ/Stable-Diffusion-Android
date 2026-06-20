#include "bonsai_dequant.h"

#include "bonsai_tensor.h"

#include <cstring>
#include <stdexcept>
#include <string>

namespace {

template <typename T>
T read_unaligned(const uint8_t* data) {
    T value {};
    std::memcpy(&value, data, sizeof(T));
    return value;
}

uint64_t last_dimension(const BonsaiTensorView& view) {
    if (view.descriptor->shape.empty()) {
        throw std::runtime_error("Bonsai tensor must have at least one dimension.");
    }
    return view.descriptor->shape.back();
}

void require_packed(const BonsaiPackedWeightViews& views) {
    if (!views.packed) {
        throw std::runtime_error(
            "Bonsai weight is dense, not packed: " + views.weight.descriptor->key
        );
    }
}

void require_row(const BonsaiPackedWeightViews& views, uint64_t row) {
    if (row >= views.leading_rows) {
        throw std::runtime_error(
            "Bonsai packed weight row is out of range: " + views.weight.descriptor->key
        );
    }
}

void require_column(const BonsaiPackedWeightViews& views, uint64_t column) {
    if (column >= views.input_values) {
        throw std::runtime_error(
            "Bonsai packed weight column is out of range: " + views.weight.descriptor->key
        );
    }
}

float read_row_scalar(
    const BonsaiTensorView& view,
    uint64_t row,
    uint64_t column
) {
    const uint64_t columns = last_dimension(view);
    const uint64_t index = row * columns + column;
    return bonsai_read_scalar_as_f32(
        view.data + index * view.dtype_byte_count,
        view.dtype
    );
}

uint32_t read_packed_word(
    const BonsaiPackedWeightViews& views,
    uint64_t row,
    uint64_t packed_word_column
) {
    const uint64_t packed_columns = last_dimension(views.weight);
    const uint64_t word_index = row * packed_columns + packed_word_column;
    return read_unaligned<uint32_t>(
        views.weight.data + word_index * views.weight.dtype_byte_count
    );
}

} // namespace

uint32_t bonsai_unpack_quantized_value(
    uint32_t word,
    int bits,
    uint64_t packed_value_index
) {
    if (bits != 1 && bits != 2 && bits != 4) {
        throw std::runtime_error("Unsupported Bonsai quantization bits: " + std::to_string(bits));
    }
    const uint32_t mask = (1U << static_cast<uint32_t>(bits)) - 1U;
    const uint32_t values_per_word = 32U / static_cast<uint32_t>(bits);
    const uint32_t shift = static_cast<uint32_t>(
        (packed_value_index % values_per_word) * static_cast<uint64_t>(bits)
    );
    return (word >> shift) & mask;
}

float bonsai_dequantize_packed_value(
    const BonsaiPackedWeightViews& views,
    uint64_t row,
    uint64_t column
) {
    require_packed(views);
    require_row(views, row);
    require_column(views, column);

    const uint64_t values_per_word = 32ULL / static_cast<uint64_t>(views.bits);
    const uint64_t word_column = column / values_per_word;
    const uint64_t group_column = column / static_cast<uint64_t>(views.group_size);
    const uint32_t word = read_packed_word(views, row, word_column);
    const uint32_t quantized = bonsai_unpack_quantized_value(word, views.bits, column);
    const float scale = read_row_scalar(views.scales, row, group_column);
    const float bias = read_row_scalar(views.biases, row, group_column);
    return static_cast<float>(quantized) * scale + bias;
}

std::vector<float> bonsai_dequantize_packed_row(
    const BonsaiPackedWeightViews& views,
    uint64_t row
) {
    require_packed(views);
    require_row(views, row);

    std::vector<float> output;
    output.reserve(static_cast<size_t>(views.input_values));
    for (uint64_t column = 0; column < views.input_values; column++) {
        output.push_back(bonsai_dequantize_packed_value(views, row, column));
    }
    return output;
}
