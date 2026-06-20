#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFluxSingleProjectionParts {
    uint64_t batch = 0;
    uint64_t sequence_length = 0;
    uint64_t dimensions = 0;
    uint64_t mlp_hidden_dimensions = 0;
    std::vector<float> query;
    std::vector<float> key;
    std::vector<float> value;
    std::vector<float> mlp_values;
};

struct BonsaiFluxHeadSequenceParts {
    uint64_t batch = 0;
    uint64_t heads = 0;
    uint64_t first_sequence_length = 0;
    uint64_t second_sequence_length = 0;
    uint64_t head_dimension = 0;
    std::vector<float> first;
    std::vector<float> second;
};

BonsaiFluxSingleProjectionParts bonsai_flux_split_single_projection(
    const std::vector<float>& fused,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    uint64_t mlp_hidden_dimensions
);

std::vector<float> bonsai_flux_sequence_to_heads(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
);

std::vector<float> bonsai_flux_heads_to_sequence(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
);

std::vector<float> bonsai_flux_concat_head_sequences(
    const std::vector<float>& first,
    const std::vector<float>& second,
    uint64_t batch,
    uint64_t heads,
    uint64_t first_sequence_length,
    uint64_t second_sequence_length,
    uint64_t head_dimension
);

BonsaiFluxHeadSequenceParts bonsai_flux_split_head_sequences(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t heads,
    uint64_t first_sequence_length,
    uint64_t second_sequence_length,
    uint64_t head_dimension
);

std::vector<float> bonsai_flux_concat_last_dimension(
    const std::vector<float>& first,
    const std::vector<float>& second,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t first_dimensions,
    uint64_t second_dimensions
);
