#include "bonsai_flux_modulation.h"

#include <cmath>
#include <limits>
#include <stdexcept>
#include <string>

namespace {

size_t checked_multiply_size(uint64_t left, uint64_t right, const char* label) {
    const uint64_t limit = static_cast<uint64_t>(std::numeric_limits<size_t>::max());
    if (left != 0 && right > limit / left) {
        throw std::runtime_error(std::string("Bonsai Flux modulation size overflow: ") + label);
    }
    return static_cast<size_t>(left * right);
}

size_t checked_modulation_size(
    uint64_t batch,
    uint64_t chunks,
    uint64_t dimensions,
    const char* label
) {
    const size_t batch_chunks = checked_multiply_size(batch, chunks, label);
    if (dimensions != 0 &&
        batch_chunks > std::numeric_limits<size_t>::max() / static_cast<size_t>(dimensions)) {
        throw std::runtime_error(std::string("Bonsai Flux modulation size overflow: ") + label);
    }
    return batch_chunks * static_cast<size_t>(dimensions);
}

void require_positive_shape(uint64_t batch, uint64_t dimensions, const char* label) {
    if (batch == 0 || dimensions == 0) {
        throw std::runtime_error(std::string("Bonsai Flux modulation shape must be positive: ") + label);
    }
}

void copy_chunk(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t chunks,
    uint64_t dimensions,
    uint64_t chunk,
    std::vector<float>* output
) {
    output->assign(checked_multiply_size(batch, dimensions, "chunk"), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t index = 0; index < dimensions; index++) {
            const size_t source_index = static_cast<size_t>(
                (batch_index * chunks + chunk) * dimensions + index
            );
            const size_t target_index = static_cast<size_t>(
                batch_index * dimensions + index
            );
            (*output)[target_index] = values[source_index];
        }
    }
}

void require_input_shape(
    const std::vector<float>& input,
    const std::vector<float>& shift,
    const std::vector<float>& scale,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions
) {
    if (batch == 0 || sequence_length == 0 || dimensions == 0) {
        throw std::runtime_error("Bonsai Flux modulated LayerNorm shape must be positive.");
    }
    const size_t input_size = checked_modulation_size(
        batch,
        sequence_length,
        dimensions,
        "layer norm"
    );
    const size_t modulation_size = checked_multiply_size(batch, dimensions, "layer norm modulation");
    if (input.size() != input_size ||
        shift.size() != modulation_size ||
        scale.size() != modulation_size) {
        throw std::runtime_error("Bonsai Flux modulated LayerNorm input shape mismatch.");
    }
}

} // namespace

BonsaiFluxSingleModulation bonsai_flux_split_single_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
) {
    require_positive_shape(batch, dimensions, "single");
    if (values.size() != checked_modulation_size(batch, 3, dimensions, "single")) {
        throw std::runtime_error("Bonsai Flux single modulation shape mismatch.");
    }

    BonsaiFluxSingleModulation output {
        batch,
        dimensions,
        {},
        {},
        {},
    };
    copy_chunk(values, batch, 3, dimensions, 0, &output.shift);
    copy_chunk(values, batch, 3, dimensions, 1, &output.scale);
    copy_chunk(values, batch, 3, dimensions, 2, &output.gate);
    return output;
}

BonsaiFluxDoubleModulation bonsai_flux_split_double_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
) {
    require_positive_shape(batch, dimensions, "double");
    if (values.size() != checked_modulation_size(batch, 6, dimensions, "double")) {
        throw std::runtime_error("Bonsai Flux double modulation shape mismatch.");
    }

    BonsaiFluxDoubleModulation output {
        batch,
        dimensions,
        {},
        {},
        {},
        {},
        {},
        {},
    };
    copy_chunk(values, batch, 6, dimensions, 0, &output.shift_msa);
    copy_chunk(values, batch, 6, dimensions, 1, &output.scale_msa);
    copy_chunk(values, batch, 6, dimensions, 2, &output.gate_msa);
    copy_chunk(values, batch, 6, dimensions, 3, &output.shift_mlp);
    copy_chunk(values, batch, 6, dimensions, 4, &output.scale_mlp);
    copy_chunk(values, batch, 6, dimensions, 5, &output.gate_mlp);
    return output;
}

BonsaiFluxNormOutModulation bonsai_flux_split_norm_out_modulation(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t dimensions
) {
    require_positive_shape(batch, dimensions, "norm out");
    if (values.size() != checked_modulation_size(batch, 2, dimensions, "norm out")) {
        throw std::runtime_error("Bonsai Flux norm-out modulation shape mismatch.");
    }

    BonsaiFluxNormOutModulation output {
        batch,
        dimensions,
        {},
        {},
    };
    copy_chunk(values, batch, 2, dimensions, 0, &output.scale);
    copy_chunk(values, batch, 2, dimensions, 1, &output.shift);
    return output;
}

std::vector<float> bonsai_flux_apply_modulated_layer_norm(
    const std::vector<float>& input,
    const std::vector<float>& shift,
    const std::vector<float>& scale,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    float epsilon
) {
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai Flux modulated LayerNorm epsilon must be finite and positive.");
    }
    require_input_shape(input, shift, scale, batch, sequence_length, dimensions);

    std::vector<float> output(input.size(), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            const size_t row_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * dimensions
            );
            const size_t modulation_offset = static_cast<size_t>(batch_index * dimensions);

            double mean = 0.0;
            for (uint64_t index = 0; index < dimensions; index++) {
                mean += static_cast<double>(input[row_offset + static_cast<size_t>(index)]);
            }
            mean /= static_cast<double>(dimensions);

            double variance = 0.0;
            for (uint64_t index = 0; index < dimensions; index++) {
                const double centered =
                    static_cast<double>(input[row_offset + static_cast<size_t>(index)]) - mean;
                variance += centered * centered;
            }
            variance /= static_cast<double>(dimensions);

            const float norm_scale = 1.0F / std::sqrt(static_cast<float>(variance) + epsilon);
            for (uint64_t index = 0; index < dimensions; index++) {
                const size_t input_index = row_offset + static_cast<size_t>(index);
                const size_t modulation_index = modulation_offset + static_cast<size_t>(index);
                const float normalized =
                    (input[input_index] - static_cast<float>(mean)) * norm_scale;
                output[input_index] =
                    normalized * (1.0F + scale[modulation_index]) + shift[modulation_index];
            }
        }
    }
    return output;
}

std::vector<float> bonsai_flux_apply_gated_residual(
    const std::vector<float>& residual,
    const std::vector<float>& update,
    const std::vector<float>& gate,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions
) {
    if (batch == 0 || sequence_length == 0 || dimensions == 0) {
        throw std::runtime_error("Bonsai Flux gated residual shape must be positive.");
    }
    const size_t input_size = checked_modulation_size(
        batch,
        sequence_length,
        dimensions,
        "gated residual"
    );
    const size_t gate_size = checked_multiply_size(batch, dimensions, "gated residual gate");
    if (residual.size() != input_size || update.size() != input_size || gate.size() != gate_size) {
        throw std::runtime_error("Bonsai Flux gated residual input shape mismatch.");
    }

    std::vector<float> output(residual.size(), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            const size_t row_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * dimensions
            );
            const size_t gate_offset = static_cast<size_t>(batch_index * dimensions);
            for (uint64_t index = 0; index < dimensions; index++) {
                const size_t value_index = row_offset + static_cast<size_t>(index);
                const size_t gate_index = gate_offset + static_cast<size_t>(index);
                output[value_index] = residual[value_index] + gate[gate_index] * update[value_index];
            }
        }
    }
    return output;
}
