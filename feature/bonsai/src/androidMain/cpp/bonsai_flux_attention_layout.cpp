#include "bonsai_flux_attention_layout.h"

#include <limits>
#include <stdexcept>
#include <string>

namespace {

size_t checked_size(uint64_t left, uint64_t right, const char* label) {
    const uint64_t limit = static_cast<uint64_t>(std::numeric_limits<size_t>::max());
    if (left != 0 && right > limit / left) {
        throw std::runtime_error(std::string("Bonsai Flux attention layout size overflow: ") + label);
    }
    return static_cast<size_t>(left * right);
}

uint64_t checked_u64_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai Flux attention layout size overflow: ") + label);
    }
    return left * right;
}

size_t checked_size_3(uint64_t first, uint64_t second, uint64_t third, const char* label) {
    const size_t first_second = checked_size(first, second, label);
    if (third != 0 &&
        first_second > std::numeric_limits<size_t>::max() / static_cast<size_t>(third)) {
        throw std::runtime_error(std::string("Bonsai Flux attention layout size overflow: ") + label);
    }
    return first_second * static_cast<size_t>(third);
}

uint64_t checked_width(
    uint64_t dimensions,
    uint64_t mlp_hidden_dimensions
) {
    if (dimensions > (std::numeric_limits<uint64_t>::max() / 3U)) {
        throw std::runtime_error("Bonsai Flux single projection width overflow.");
    }
    const uint64_t qkv_width = dimensions * 3U;
    if (mlp_hidden_dimensions > (std::numeric_limits<uint64_t>::max() / 2U)) {
        throw std::runtime_error("Bonsai Flux single projection width overflow.");
    }
    const uint64_t mlp_width = mlp_hidden_dimensions * 2U;
    if (qkv_width > std::numeric_limits<uint64_t>::max() - mlp_width) {
        throw std::runtime_error("Bonsai Flux single projection width overflow.");
    }
    return qkv_width + mlp_width;
}

void require_positive(uint64_t value, const char* label) {
    if (value == 0) {
        throw std::runtime_error(std::string("Bonsai Flux attention layout value must be positive: ") + label);
    }
}

void copy_projection_chunk(
    const std::vector<float>& fused,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t fused_width,
    uint64_t chunk_offset,
    uint64_t chunk_width,
    std::vector<float>* output
) {
    output->assign(checked_size_3(batch, sequence_length, chunk_width, "projection chunk"), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            const size_t source_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * fused_width + chunk_offset
            );
            const size_t target_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * chunk_width
            );
            for (uint64_t index = 0; index < chunk_width; index++) {
                (*output)[target_offset + static_cast<size_t>(index)] =
                    fused[source_offset + static_cast<size_t>(index)];
            }
        }
    }
}

size_t head_index(
    uint64_t batch_index,
    uint64_t head,
    uint64_t token,
    uint64_t column,
    uint64_t heads,
    uint64_t sequence_length,
    uint64_t head_dimension
) {
    return static_cast<size_t>(
        ((batch_index * heads + head) * sequence_length + token) * head_dimension + column
    );
}

size_t sequence_index(
    uint64_t batch_index,
    uint64_t token,
    uint64_t head,
    uint64_t column,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    return static_cast<size_t>(
        ((batch_index * sequence_length + token) * heads + head) * head_dimension + column
    );
}

} // namespace

BonsaiFluxSingleProjectionParts bonsai_flux_split_single_projection(
    const std::vector<float>& fused,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    uint64_t mlp_hidden_dimensions
) {
    require_positive(batch, "batch");
    require_positive(sequence_length, "sequence");
    require_positive(dimensions, "dimensions");
    require_positive(mlp_hidden_dimensions, "mlp hidden");

    const uint64_t fused_width = checked_width(dimensions, mlp_hidden_dimensions);
    if (fused.size() != checked_size_3(batch, sequence_length, fused_width, "single projection")) {
        throw std::runtime_error("Bonsai Flux single projection shape mismatch.");
    }

    BonsaiFluxSingleProjectionParts output {
        batch,
        sequence_length,
        dimensions,
        mlp_hidden_dimensions,
        {},
        {},
        {},
        {},
    };
    copy_projection_chunk(
        fused,
        batch,
        sequence_length,
        fused_width,
        0,
        dimensions,
        &output.query
    );
    copy_projection_chunk(
        fused,
        batch,
        sequence_length,
        fused_width,
        dimensions,
        dimensions,
        &output.key
    );
    copy_projection_chunk(
        fused,
        batch,
        sequence_length,
        fused_width,
        dimensions * 2U,
        dimensions,
        &output.value
    );
    copy_projection_chunk(
        fused,
        batch,
        sequence_length,
        fused_width,
        dimensions * 3U,
        mlp_hidden_dimensions * 2U,
        &output.mlp_values
    );
    return output;
}

std::vector<float> bonsai_flux_sequence_to_heads(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    require_positive(batch, "batch");
    require_positive(sequence_length, "sequence");
    require_positive(heads, "heads");
    require_positive(head_dimension, "head dimension");
    const uint64_t dimensions = checked_u64_multiply(heads, head_dimension, "sequence dimensions");
    const uint64_t batch_heads = checked_u64_multiply(batch, heads, "batch heads");
    const size_t expected = checked_size_3(batch, sequence_length, dimensions, "sequence");
    if (input.size() != expected) {
        throw std::runtime_error("Bonsai Flux sequence-to-heads shape mismatch.");
    }

    std::vector<float> output(checked_size_3(batch_heads, sequence_length, head_dimension, "heads"), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            for (uint64_t head = 0; head < heads; head++) {
                for (uint64_t column = 0; column < head_dimension; column++) {
                    output[head_index(
                        batch_index,
                        head,
                        token,
                        column,
                        heads,
                        sequence_length,
                        head_dimension
                    )] = input[sequence_index(
                        batch_index,
                        token,
                        head,
                        column,
                        sequence_length,
                        heads,
                        head_dimension
                    )];
                }
            }
        }
    }
    return output;
}

std::vector<float> bonsai_flux_heads_to_sequence(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    require_positive(batch, "batch");
    require_positive(sequence_length, "sequence");
    require_positive(heads, "heads");
    require_positive(head_dimension, "head dimension");
    const uint64_t dimensions = checked_u64_multiply(heads, head_dimension, "sequence dimensions");
    const uint64_t batch_heads = checked_u64_multiply(batch, heads, "batch heads");
    const size_t expected = checked_size_3(batch_heads, sequence_length, head_dimension, "heads");
    if (input.size() != expected) {
        throw std::runtime_error("Bonsai Flux heads-to-sequence shape mismatch.");
    }

    std::vector<float> output(checked_size_3(batch, sequence_length, dimensions, "sequence"), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            for (uint64_t head = 0; head < heads; head++) {
                for (uint64_t column = 0; column < head_dimension; column++) {
                    output[sequence_index(
                        batch_index,
                        token,
                        head,
                        column,
                        sequence_length,
                        heads,
                        head_dimension
                    )] = input[head_index(
                        batch_index,
                        head,
                        token,
                        column,
                        heads,
                        sequence_length,
                        head_dimension
                    )];
                }
            }
        }
    }
    return output;
}

std::vector<float> bonsai_flux_concat_head_sequences(
    const std::vector<float>& first,
    const std::vector<float>& second,
    uint64_t batch,
    uint64_t heads,
    uint64_t first_sequence_length,
    uint64_t second_sequence_length,
    uint64_t head_dimension
) {
    require_positive(batch, "batch");
    require_positive(heads, "heads");
    require_positive(first_sequence_length, "first sequence");
    require_positive(second_sequence_length, "second sequence");
    require_positive(head_dimension, "head dimension");
    const uint64_t batch_heads = checked_u64_multiply(batch, heads, "batch heads");
    const size_t first_size = checked_size_3(batch_heads, first_sequence_length, head_dimension, "first");
    const size_t second_size = checked_size_3(batch_heads, second_sequence_length, head_dimension, "second");
    if (first.size() != first_size || second.size() != second_size) {
        throw std::runtime_error("Bonsai Flux concat-head-sequences shape mismatch.");
    }

    const uint64_t combined_sequence_length = first_sequence_length + second_sequence_length;
    if (combined_sequence_length < first_sequence_length) {
        throw std::runtime_error("Bonsai Flux concat-head-sequences length overflow.");
    }
    std::vector<float> output(
        checked_size_3(batch_heads, combined_sequence_length, head_dimension, "concat"),
        0.0F
    );
    for (uint64_t batch_head = 0; batch_head < batch_heads; batch_head++) {
        for (uint64_t token = 0; token < first_sequence_length; token++) {
            for (uint64_t column = 0; column < head_dimension; column++) {
                const size_t source = static_cast<size_t>(
                    (batch_head * first_sequence_length + token) * head_dimension + column
                );
                const size_t target = static_cast<size_t>(
                    (batch_head * combined_sequence_length + token) * head_dimension + column
                );
                output[target] = first[source];
            }
        }
        for (uint64_t token = 0; token < second_sequence_length; token++) {
            for (uint64_t column = 0; column < head_dimension; column++) {
                const size_t source = static_cast<size_t>(
                    (batch_head * second_sequence_length + token) * head_dimension + column
                );
                const size_t target = static_cast<size_t>(
                    (
                        batch_head * combined_sequence_length +
                        first_sequence_length +
                        token
                    ) * head_dimension + column
                );
                output[target] = second[source];
            }
        }
    }
    return output;
}

BonsaiFluxHeadSequenceParts bonsai_flux_split_head_sequences(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t heads,
    uint64_t first_sequence_length,
    uint64_t second_sequence_length,
    uint64_t head_dimension
) {
    require_positive(batch, "batch");
    require_positive(heads, "heads");
    require_positive(first_sequence_length, "first sequence");
    require_positive(second_sequence_length, "second sequence");
    require_positive(head_dimension, "head dimension");
    const uint64_t batch_heads = checked_u64_multiply(batch, heads, "batch heads");
    const uint64_t combined_sequence_length = first_sequence_length + second_sequence_length;
    if (combined_sequence_length < first_sequence_length) {
        throw std::runtime_error("Bonsai Flux split-head-sequences length overflow.");
    }
    const size_t expected = checked_size_3(
        batch_heads,
        combined_sequence_length,
        head_dimension,
        "split"
    );
    if (input.size() != expected) {
        throw std::runtime_error("Bonsai Flux split-head-sequences shape mismatch.");
    }

    BonsaiFluxHeadSequenceParts output {
        batch,
        heads,
        first_sequence_length,
        second_sequence_length,
        head_dimension,
        {},
        {},
    };
    output.first.assign(
        checked_size_3(batch_heads, first_sequence_length, head_dimension, "split first"),
        0.0F
    );
    output.second.assign(
        checked_size_3(batch_heads, second_sequence_length, head_dimension, "split second"),
        0.0F
    );

    for (uint64_t batch_head = 0; batch_head < batch_heads; batch_head++) {
        for (uint64_t token = 0; token < first_sequence_length; token++) {
            for (uint64_t column = 0; column < head_dimension; column++) {
                const size_t source = static_cast<size_t>(
                    (batch_head * combined_sequence_length + token) * head_dimension + column
                );
                const size_t target = static_cast<size_t>(
                    (batch_head * first_sequence_length + token) * head_dimension + column
                );
                output.first[target] = input[source];
            }
        }
        for (uint64_t token = 0; token < second_sequence_length; token++) {
            for (uint64_t column = 0; column < head_dimension; column++) {
                const size_t source = static_cast<size_t>(
                    (
                        batch_head * combined_sequence_length +
                        first_sequence_length +
                        token
                    ) * head_dimension + column
                );
                const size_t target = static_cast<size_t>(
                    (batch_head * second_sequence_length + token) * head_dimension + column
                );
                output.second[target] = input[source];
            }
        }
    }
    return output;
}

std::vector<float> bonsai_flux_concat_last_dimension(
    const std::vector<float>& first,
    const std::vector<float>& second,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t first_dimensions,
    uint64_t second_dimensions
) {
    require_positive(batch, "batch");
    require_positive(sequence_length, "sequence");
    require_positive(first_dimensions, "first dimensions");
    require_positive(second_dimensions, "second dimensions");
    const size_t first_size = checked_size_3(
        batch,
        sequence_length,
        first_dimensions,
        "concat last first"
    );
    const size_t second_size = checked_size_3(
        batch,
        sequence_length,
        second_dimensions,
        "concat last second"
    );
    if (first.size() != first_size || second.size() != second_size) {
        throw std::runtime_error("Bonsai Flux concat-last-dimension shape mismatch.");
    }

    const uint64_t output_dimensions = first_dimensions + second_dimensions;
    if (output_dimensions < first_dimensions) {
        throw std::runtime_error("Bonsai Flux concat-last-dimension width overflow.");
    }
    std::vector<float> output(
        checked_size_3(batch, sequence_length, output_dimensions, "concat last"),
        0.0F
    );
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < sequence_length; token++) {
            const size_t first_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * first_dimensions
            );
            const size_t second_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * second_dimensions
            );
            const size_t output_offset = static_cast<size_t>(
                (batch_index * sequence_length + token) * output_dimensions
            );
            for (uint64_t index = 0; index < first_dimensions; index++) {
                output[output_offset + static_cast<size_t>(index)] =
                    first[first_offset + static_cast<size_t>(index)];
            }
            for (uint64_t index = 0; index < second_dimensions; index++) {
                output[output_offset + static_cast<size_t>(first_dimensions + index)] =
                    second[second_offset + static_cast<size_t>(index)];
            }
        }
    }
    return output;
}
