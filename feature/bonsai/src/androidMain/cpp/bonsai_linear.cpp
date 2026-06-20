#include "bonsai_linear.h"

#include "bonsai_tensor.h"
#include "bonsai_vulkan.h"

#include <algorithm>
#include <cstddef>
#include <exception>
#include <limits>
#include <mutex>
#include <stdexcept>
#include <string>
#include <thread>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai linear shape overflow: ") + label);
    }
    return left * right;
}

BonsaiTensorView optional_bias_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& bias_key
) {
    if (bias_key.empty()) {
        return {};
    }

    const BonsaiTensorDescriptor* descriptor = index.optional(bias_key);
    if (descriptor == nullptr) {
        return {};
    }
    return storage.view(*descriptor);
}

void validate_bias(
    const BonsaiTensorView& bias,
    uint64_t output_rows,
    const std::string& weight_key
) {
    if (!bonsai_dtype_is_floating_point(bias.dtype)) {
        throw std::runtime_error("Bonsai linear bias must be floating point: " + bias.descriptor->key);
    }
    if (bias.element_count != output_rows) {
        throw std::runtime_error(
            "Bonsai linear bias size mismatch for " +
            weight_key +
            ": " +
            bias.descriptor->key
        );
    }
}

float bias_at(const BonsaiLinearViews& views, uint64_t row) {
    if (!views.has_bias) {
        return 0.0F;
    }
    return bonsai_read_scalar_as_f32(
        views.bias.data + row * views.bias.dtype_byte_count,
        views.bias.dtype
    );
}

void require_input(const BonsaiLinearViews& views, const std::vector<float>& input) {
    if (input.size() != static_cast<size_t>(views.input_values)) {
        throw std::runtime_error("Bonsai linear input size mismatch.");
    }
}

void require_row(const BonsaiLinearViews& views, uint64_t row) {
    if (row >= views.output_rows) {
        throw std::runtime_error("Bonsai linear row is out of range.");
    }
}

void add_bias_to_output(const BonsaiLinearViews& views, float* output) {
    if (!views.has_bias) {
        return;
    }
    for (uint64_t row = 0; row < views.output_rows; row++) {
        output[row] += bias_at(views, row);
    }
}

void linear_into_unchecked(
    const BonsaiLinearViews& views,
    const float* input,
    float* output
) {
    switch (views.kind) {
        case BonsaiLinearWeightKind::Dense:
            bonsai_dense_matvec_into(views.dense, input, output);
            break;
        case BonsaiLinearWeightKind::Packed:
            bonsai_quantized_matvec_into(views.packed, input, output);
            break;
    }
    add_bias_to_output(views, output);
}

uint64_t linear_sequence_worker_count(
    uint64_t token_count,
    uint64_t input_values,
    uint64_t output_rows
) {
    if (token_count < 4 || input_values < 512 || output_rows < 512) {
        return 1;
    }
    const unsigned hardware_threads = std::thread::hardware_concurrency();
    const uint64_t available_threads = hardware_threads == 0
        ? 2U
        : static_cast<uint64_t>(hardware_threads);
    return std::max<uint64_t>(
        1,
        std::min<uint64_t>({ token_count, available_threads, 4U })
    );
}

} // namespace

BonsaiLinearViews bonsai_require_dense_linear_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& weight_key,
    const std::string& bias_key
) {
    BonsaiLinearViews views;
    views.kind = BonsaiLinearWeightKind::Dense;
    views.dense = bonsai_require_dense_weight_view(storage, index, weight_key);
    views.output_rows = views.dense.leading_rows;
    views.input_values = views.dense.input_values;
    views.bias = optional_bias_view(storage, index, bias_key);
    views.has_bias = views.bias.descriptor != nullptr;
    if (views.has_bias) {
        validate_bias(views.bias, views.output_rows, weight_key);
    }
    return views;
}

BonsaiLinearViews bonsai_require_packed_linear_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor,
    const std::string& bias_key
) {
    BonsaiLinearViews views;
    views.kind = descriptor.packed ? BonsaiLinearWeightKind::Packed : BonsaiLinearWeightKind::Dense;
    if (descriptor.packed) {
        views.packed = bonsai_require_packed_weight_views(storage, index, descriptor);
        views.output_rows = views.packed.leading_rows;
        views.input_values = views.packed.input_values;
    } else {
        views.dense = bonsai_require_dense_weight_view(storage, index, descriptor.weight_key);
        views.output_rows = views.dense.leading_rows;
        views.input_values = views.dense.input_values;
    }

    views.bias = optional_bias_view(storage, index, bias_key);
    views.has_bias = views.bias.descriptor != nullptr;
    if (views.has_bias) {
        validate_bias(views.bias, views.output_rows, descriptor.weight_key);
    }
    return views;
}

float bonsai_linear_row(
    const BonsaiLinearViews& views,
    const std::vector<float>& input,
    uint64_t row
) {
    require_input(views, input);
    require_row(views, row);

    float output = 0.0F;
    switch (views.kind) {
        case BonsaiLinearWeightKind::Dense:
            output = bonsai_dense_matvec_row(views.dense, input, row);
            break;
        case BonsaiLinearWeightKind::Packed:
            output = bonsai_quantized_matvec_row(views.packed, input, row);
            break;
    }
    return output + bias_at(views, row);
}

std::vector<float> bonsai_linear(
    const BonsaiLinearViews& views,
    const std::vector<float>& input
) {
    require_input(views, input);

    std::vector<float> output(static_cast<size_t>(views.output_rows));
    linear_into_unchecked(views, input.data(), output.data());
    return output;
}

std::vector<float> bonsai_linear_sequence(
    const BonsaiLinearViews& views,
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length
) {
    if (batch == 0 || sequence_length == 0) {
        throw std::runtime_error("Bonsai linear sequence shape must be positive.");
    }

    const uint64_t token_count = checked_multiply(batch, sequence_length, "sequence tokens");
    const uint64_t expected_input = checked_multiply(
        token_count,
        views.input_values,
        "sequence input"
    );
    if (input.size() != static_cast<size_t>(expected_input)) {
        throw std::runtime_error("Bonsai linear sequence input size mismatch.");
    }

    const uint64_t expected_output = checked_multiply(
        token_count,
        views.output_rows,
        "sequence output"
    );
    if (expected_output > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error("Bonsai linear sequence output is too large.");
    }

    std::vector<float> output(static_cast<size_t>(expected_output));
    if (views.kind == BonsaiLinearWeightKind::Packed &&
        bonsai_vulkan_quantized_matvec_sequence_into(
            views.packed,
            input.data(),
            output.data(),
            token_count
        )) {
        for (uint64_t token = 0; token < token_count; token++) {
            add_bias_to_output(views, output.data() + token * views.output_rows);
        }
        return output;
    }

    const uint64_t worker_count = linear_sequence_worker_count(
        token_count,
        views.input_values,
        views.output_rows
    );
    const auto run_range = [&views, &input, &output](uint64_t begin, uint64_t end) {
        for (uint64_t token = begin; token < end; token++) {
            linear_into_unchecked(
                views,
                input.data() + token * views.input_values,
                output.data() + token * views.output_rows
            );
        }
    };

    if (worker_count == 1) {
        run_range(0, token_count);
        return output;
    }

    std::vector<std::thread> workers;
    workers.reserve(static_cast<size_t>(worker_count));
    std::exception_ptr first_error = nullptr;
    std::mutex error_mutex;
    const uint64_t chunk_size = (token_count + worker_count - 1U) / worker_count;
    for (uint64_t worker = 0; worker < worker_count; worker++) {
        const uint64_t begin = worker * chunk_size;
        const uint64_t end = std::min(token_count, begin + chunk_size);
        if (begin >= end) {
            continue;
        }
        workers.emplace_back([&, begin, end]() {
            try {
                run_range(begin, end);
            } catch (...) {
                std::lock_guard<std::mutex> lock(error_mutex);
                if (first_error == nullptr) {
                    first_error = std::current_exception();
                }
            }
        });
    }
    for (std::thread& worker : workers) {
        worker.join();
    }
    if (first_error != nullptr) {
        std::rethrow_exception(first_error);
    }
    return output;
}

uint64_t bonsai_linear_byte_count(const BonsaiLinearViews& views) {
    uint64_t bytes = 0;
    switch (views.kind) {
        case BonsaiLinearWeightKind::Dense:
            bytes = views.dense.weight.byte_count;
            break;
        case BonsaiLinearWeightKind::Packed:
            bytes = bonsai_packed_weight_byte_count(views.packed);
            break;
    }
    if (views.has_bias) {
        bytes += views.bias.byte_count;
    }
    return bytes;
}
