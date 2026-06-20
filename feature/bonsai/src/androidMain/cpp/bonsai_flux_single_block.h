#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFluxSingleBlockReferenceOutput {
    uint64_t batch = 0;
    uint64_t sequence_length = 0;
    uint64_t dimensions = 0;
    uint64_t out_projection_input_dimensions = 0;
    std::vector<float> normalized_hidden;
    std::vector<float> out_projection_input;
    std::vector<float> residual_output;
};

BonsaiFluxSingleBlockReferenceOutput bonsai_flux_single_block_reference(
    const std::vector<float>& hidden,
    const std::vector<float>& modulation_values,
    const std::vector<float>& fused_projection,
    const std::vector<float>& projected_update,
    const std::vector<float>& norm_q_weight,
    const std::vector<float>& norm_k_weight,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension,
    uint64_t mlp_hidden_dimensions,
    float layer_norm_epsilon,
    float rms_norm_epsilon
);
