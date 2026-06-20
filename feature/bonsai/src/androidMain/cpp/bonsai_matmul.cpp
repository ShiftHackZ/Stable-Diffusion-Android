#include "bonsai_matmul.h"

#include "bonsai_dequant.h"
#include "bonsai_tensor.h"
#include "bonsai_vulkan.h"

#include <algorithm>
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

uint64_t checked_multiply(uint64_t left, uint64_t right, const std::string& tensor_key) {
    if (left != 0 && right > UINT64_MAX / left) {
        throw std::runtime_error("Bonsai dense weight shape is too large: " + tensor_key);
    }
    return left * right;
}

uint64_t leading_row_count(const BonsaiTensorView& view) {
    if (view.descriptor->shape.empty()) {
        throw std::runtime_error("Bonsai dense weight must have at least one dimension.");
    }

    uint64_t rows = 1;
    for (size_t index = 0; index + 1 < view.descriptor->shape.size(); index++) {
        rows = checked_multiply(rows, view.descriptor->shape[index], view.descriptor->key);
    }
    return rows;
}

uint64_t last_dimension(const BonsaiTensorView& view) {
    if (view.descriptor->shape.empty()) {
        throw std::runtime_error("Bonsai dense weight must have at least one dimension.");
    }
    return view.descriptor->shape.back();
}

float dot_u2_word(uint32_t word, const float* input) {
    return input[0] * static_cast<float>(word & 0x3U) +
        input[1] * static_cast<float>((word >> 2U) & 0x3U) +
        input[2] * static_cast<float>((word >> 4U) & 0x3U) +
        input[3] * static_cast<float>((word >> 6U) & 0x3U) +
        input[4] * static_cast<float>((word >> 8U) & 0x3U) +
        input[5] * static_cast<float>((word >> 10U) & 0x3U) +
        input[6] * static_cast<float>((word >> 12U) & 0x3U) +
        input[7] * static_cast<float>((word >> 14U) & 0x3U) +
        input[8] * static_cast<float>((word >> 16U) & 0x3U) +
        input[9] * static_cast<float>((word >> 18U) & 0x3U) +
        input[10] * static_cast<float>((word >> 20U) & 0x3U) +
        input[11] * static_cast<float>((word >> 22U) & 0x3U) +
        input[12] * static_cast<float>((word >> 24U) & 0x3U) +
        input[13] * static_cast<float>((word >> 26U) & 0x3U) +
        input[14] * static_cast<float>((word >> 28U) & 0x3U) +
        input[15] * static_cast<float>((word >> 30U) & 0x3U);
}

float sum_16_values(const float* input) {
    return input[0] + input[1] + input[2] + input[3] +
        input[4] + input[5] + input[6] + input[7] +
        input[8] + input[9] + input[10] + input[11] +
        input[12] + input[13] + input[14] + input[15];
}

void require_dense_input(
    const BonsaiDenseWeightViews& views,
    const std::vector<float>& input
) {
    if (input.size() != static_cast<size_t>(views.input_values)) {
        throw std::runtime_error(
            "Bonsai dense matvec input size mismatch: " + views.weight.descriptor->key
        );
    }
}

void require_packed_input(
    const BonsaiPackedWeightViews& views,
    const std::vector<float>& input
) {
    if (!views.packed) {
        throw std::runtime_error(
            "Bonsai quantized matvec requires packed weight: " + views.weight.descriptor->key
        );
    }
    if (input.size() != static_cast<size_t>(views.input_values)) {
        throw std::runtime_error(
            "Bonsai quantized matvec input size mismatch: " + views.weight.descriptor->key
        );
    }
}

void require_io_pointers(const float* input, const float* output) {
    if (input == nullptr || output == nullptr) {
        throw std::runtime_error("Bonsai matvec input/output pointer must not be null.");
    }
}

float dense_matvec_row_unchecked(
    const BonsaiDenseWeightViews& views,
    const float* input,
    uint64_t row
) {
    float sum = 0.0F;
    const uint8_t* weight_row = views.weight.data +
        row * views.input_values * views.weight.dtype_byte_count;
    for (uint64_t column = 0; column < views.input_values; column++) {
        const float weight = bonsai_read_scalar_as_f32(
            weight_row + column * views.weight.dtype_byte_count,
            views.weight.dtype
        );
        sum += input[column] * weight;
    }
    return sum;
}

float quantized_matvec_row_unchecked(
    const BonsaiPackedWeightViews& views,
    const float* input,
    uint64_t row,
    const float* group_input_sums
) {
    const uint64_t values_per_word = 32ULL / static_cast<uint64_t>(views.bits);
    const uint64_t packed_columns = last_dimension(views.weight);
    const uint64_t scale_groups = last_dimension(views.scales);
    const uint64_t group_size = static_cast<uint64_t>(views.group_size);
    const uint32_t mask = (1U << static_cast<uint32_t>(views.bits)) - 1U;

    const uint8_t* packed_row = views.weight.data +
        row * packed_columns * views.weight.dtype_byte_count;
    const uint8_t* scale_row = views.scales.data +
        row * scale_groups * views.scales.dtype_byte_count;
    const uint8_t* bias_row = views.biases.data +
        row * scale_groups * views.biases.dtype_byte_count;

    float sum = 0.0F;
    for (uint64_t group = 0; group < scale_groups; group++) {
        const float scale = bonsai_read_scalar_as_f32(
            scale_row + group * views.scales.dtype_byte_count,
            views.scales.dtype
        );
        const float bias = bonsai_read_scalar_as_f32(
            bias_row + group * views.biases.dtype_byte_count,
            views.biases.dtype
        );
        const uint64_t group_start = group * group_size;
        const uint64_t group_end = std::min(group_start + group_size, views.input_values);
        float quantized_sum = 0.0F;
        float input_sum = group_input_sums == nullptr ? 0.0F : group_input_sums[group];
        uint64_t column = group_start;
        while (column < group_end) {
            const uint64_t word_column = column / values_per_word;
            const uint64_t first_offset = column % values_per_word;
            const uint64_t word_base_column = word_column * values_per_word;
            const uint64_t last_offset = std::min(values_per_word, group_end - word_base_column);
            const uint32_t word = read_unaligned<uint32_t>(
                packed_row + word_column * views.weight.dtype_byte_count
            );
            if (views.bits == 2 &&
                first_offset == 0 &&
                last_offset == values_per_word &&
                word_base_column + values_per_word <= views.input_values) {
                const float* word_input = input + word_base_column;
                quantized_sum += dot_u2_word(word, word_input);
                if (group_input_sums == nullptr) {
                    input_sum += sum_16_values(word_input);
                }
            } else {
                for (uint64_t offset = first_offset; offset < last_offset; offset++) {
                    const uint64_t input_index = word_base_column + offset;
                    const float input_value = input[input_index];
                    const uint32_t quantized =
                        (word >> (offset * static_cast<uint64_t>(views.bits))) & mask;
                    quantized_sum += input_value * static_cast<float>(quantized);
                    if (group_input_sums == nullptr) {
                        input_sum += input_value;
                    }
                }
            }
            column = word_base_column + last_offset;
        }
        sum += quantized_sum * scale + input_sum * bias;
    }
    return sum;
}

std::vector<float> packed_group_input_sums(
    const BonsaiPackedWeightViews& views,
    const float* input
) {
    const uint64_t scale_groups = last_dimension(views.scales);
    const uint64_t group_size = static_cast<uint64_t>(views.group_size);
    std::vector<float> sums(static_cast<size_t>(scale_groups), 0.0F);
    for (uint64_t group = 0; group < scale_groups; group++) {
        const uint64_t group_start = group * group_size;
        const uint64_t group_end = std::min(group_start + group_size, views.input_values);
        float sum = 0.0F;
        for (uint64_t column = group_start; column < group_end; column++) {
            sum += input[column];
        }
        sums[static_cast<size_t>(group)] = sum;
    }
    return sums;
}

} // namespace

BonsaiDenseWeightViews bonsai_require_dense_weight_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& key
) {
    BonsaiTensorView weight = storage.require_view(index, key);
    if (!bonsai_dtype_is_floating_point(weight.dtype)) {
        throw std::runtime_error("Bonsai dense weight must be floating point: " + key);
    }
    return BonsaiDenseWeightViews {
        weight,
        leading_row_count(weight),
        last_dimension(weight),
    };
}

float bonsai_dense_matvec_row(
    const BonsaiDenseWeightViews& views,
    const std::vector<float>& input,
    uint64_t row
) {
    require_dense_input(views, input);
    if (row >= views.leading_rows) {
        throw std::runtime_error(
            "Bonsai dense matvec row is out of range: " + views.weight.descriptor->key
        );
    }

    return dense_matvec_row_unchecked(views, input.data(), row);
}

std::vector<float> bonsai_dense_matvec(
    const BonsaiDenseWeightViews& views,
    const std::vector<float>& input
) {
    require_dense_input(views, input);

    std::vector<float> output(static_cast<size_t>(views.leading_rows));
    bonsai_dense_matvec_into(views, input.data(), output.data());
    return output;
}

void bonsai_dense_matvec_into(
    const BonsaiDenseWeightViews& views,
    const float* input,
    float* output
) {
    require_io_pointers(input, output);
    for (uint64_t row = 0; row < views.leading_rows; row++) {
        output[row] = dense_matvec_row_unchecked(views, input, row);
    }
}

float bonsai_quantized_matvec_row(
    const BonsaiPackedWeightViews& views,
    const std::vector<float>& input,
    uint64_t row
) {
    require_packed_input(views, input);
    if (row >= views.leading_rows) {
        throw std::runtime_error(
            "Bonsai quantized matvec row is out of range: " + views.weight.descriptor->key
        );
    }

    return quantized_matvec_row_unchecked(views, input.data(), row, nullptr);
}

std::vector<float> bonsai_quantized_matvec(
    const BonsaiPackedWeightViews& views,
    const std::vector<float>& input
) {
    require_packed_input(views, input);

    std::vector<float> output(static_cast<size_t>(views.leading_rows));
    bonsai_quantized_matvec_into(views, input.data(), output.data());
    return output;
}

void bonsai_quantized_matvec_into(
    const BonsaiPackedWeightViews& views,
    const float* input,
    float* output
) {
    require_io_pointers(input, output);
    if (!views.packed) {
        throw std::runtime_error(
            "Bonsai quantized matvec requires packed weight: " + views.weight.descriptor->key
        );
    }
    if (bonsai_vulkan_quantized_matvec_into(views, input, output)) {
        return;
    }
    const std::vector<float> group_input_sums = packed_group_input_sums(views, input);
    for (uint64_t row = 0; row < views.leading_rows; row++) {
        output[row] = quantized_matvec_row_unchecked(views, input, row, group_input_sums.data());
    }
}
