#pragma once

#include "bonsai_linear.h"
#include "bonsai_norm.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <array>
#include <cstdint>
#include <vector>

struct BonsaiFluxTransformerInventorySummary {
    uint64_t double_block_count = 0;
    uint64_t single_block_count = 0;
    uint64_t logical_tensor_count = 0;
};

struct BonsaiFluxSingleBlockViews {
    BonsaiLinearViews qkv_mlp_proj;
    BonsaiLinearViews out_proj;
    BonsaiRmsNormWeightViews norm_q;
    BonsaiRmsNormWeightViews norm_k;
    uint64_t dimensions = 0;
    uint64_t heads = 0;
    uint64_t head_dimension = 0;
    uint64_t mlp_hidden_dimensions = 0;
};

struct BonsaiFluxDoubleBlockViews {
    BonsaiLinearViews to_q;
    BonsaiLinearViews to_k;
    BonsaiLinearViews to_v;
    BonsaiLinearViews add_q;
    BonsaiLinearViews add_k;
    BonsaiLinearViews add_v;
    BonsaiLinearViews to_out;
    BonsaiLinearViews to_add_out;
    BonsaiLinearViews ff_in;
    BonsaiLinearViews ff_out;
    BonsaiLinearViews ff_context_in;
    BonsaiLinearViews ff_context_out;
    BonsaiRmsNormWeightViews norm_q;
    BonsaiRmsNormWeightViews norm_k;
    BonsaiRmsNormWeightViews norm_added_q;
    BonsaiRmsNormWeightViews norm_added_k;
    uint64_t dimensions = 0;
    uint64_t heads = 0;
    uint64_t head_dimension = 0;
    uint64_t mlp_hidden_dimensions = 0;
};

struct BonsaiFluxTransformerViews {
    BonsaiLinearViews x_embedder;
    BonsaiLinearViews context_embedder;
    BonsaiLinearViews timestep_linear1;
    BonsaiLinearViews timestep_linear2;
    BonsaiLinearViews double_modulation_img;
    BonsaiLinearViews double_modulation_txt;
    BonsaiLinearViews single_modulation;
    BonsaiLinearViews norm_out_linear;
    BonsaiLinearViews proj_out;
    std::vector<BonsaiFluxDoubleBlockViews> double_blocks;
    std::vector<BonsaiFluxSingleBlockViews> single_blocks;
    uint64_t dimensions = 0;
    uint64_t text_hidden_size = 0;
    uint64_t latent_channels = 0;
    uint64_t timestep_embedding_size = 0;
};

struct BonsaiFluxTransformerOutput {
    std::vector<float> values;
    uint64_t batch = 1;
    uint64_t sequence_length = 0;
    uint64_t channels = 0;
};

BonsaiFluxTransformerInventorySummary bonsai_require_flux_transformer_tensors(
    const BonsaiSafetensorsIndex& index,
    int bits,
    int group_size
);

BonsaiFluxTransformerViews bonsai_require_flux_transformer_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    int bits,
    int group_size
);

BonsaiFluxTransformerOutput bonsai_flux_transformer_forward(
    const BonsaiFluxTransformerViews& views,
    const std::vector<float>& latent_tokens,
    const std::vector<float>& prompt_embeddings,
    const std::vector<std::array<float, 4>>& image_ids,
    const std::vector<std::array<float, 4>>& text_ids,
    float timestep
);

uint64_t bonsai_flux_single_block_byte_count(const BonsaiFluxSingleBlockViews& views);

uint64_t bonsai_flux_double_block_byte_count(const BonsaiFluxDoubleBlockViews& views);

uint64_t bonsai_flux_transformer_byte_count(const BonsaiFluxTransformerViews& views);
