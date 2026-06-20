#pragma once

#include <cstdint>
#include <vector>

struct BonsaiFluxDoubleBlockReferenceOutput {
    uint64_t batch = 0;
    uint64_t text_sequence_length = 0;
    uint64_t image_sequence_length = 0;
    uint64_t dimensions = 0;
    std::vector<float> normalized_text_msa;
    std::vector<float> normalized_image_msa;
    std::vector<float> attention_text;
    std::vector<float> attention_image;
    std::vector<float> normalized_text_mlp;
    std::vector<float> normalized_image_mlp;
    std::vector<float> text_output;
    std::vector<float> image_output;
};

BonsaiFluxDoubleBlockReferenceOutput bonsai_flux_double_block_reference(
    const std::vector<float>& text,
    const std::vector<float>& image,
    const std::vector<float>& text_modulation_values,
    const std::vector<float>& image_modulation_values,
    const std::vector<float>& text_query_projection,
    const std::vector<float>& text_key_projection,
    const std::vector<float>& text_value_projection,
    const std::vector<float>& image_query_projection,
    const std::vector<float>& image_key_projection,
    const std::vector<float>& image_value_projection,
    const std::vector<float>& text_attention_update,
    const std::vector<float>& image_attention_update,
    const std::vector<float>& text_mlp_update,
    const std::vector<float>& image_mlp_update,
    const std::vector<float>& text_query_norm_weight,
    const std::vector<float>& text_key_norm_weight,
    const std::vector<float>& image_query_norm_weight,
    const std::vector<float>& image_key_norm_weight,
    const std::vector<float>& text_cos_values,
    const std::vector<float>& text_sin_values,
    const std::vector<float>& image_cos_values,
    const std::vector<float>& image_sin_values,
    uint64_t batch,
    uint64_t text_sequence_length,
    uint64_t image_sequence_length,
    uint64_t heads,
    uint64_t head_dimension,
    float layer_norm_epsilon,
    float rms_norm_epsilon
);
