#pragma once

#include "bonsai_embedding.h"
#include "bonsai_linear.h"
#include "bonsai_norm.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <vector>

struct BonsaiQwenLayerProbeSummary {
    uint64_t bytes = 0;
    double checksum = 0.0;
};

struct BonsaiQwenInventorySummary {
    uint64_t layer_count = 0;
    uint64_t logical_tensor_count = 0;
};

struct BonsaiQwenAttentionViews {
    BonsaiLinearViews q_proj;
    BonsaiLinearViews k_proj;
    BonsaiLinearViews v_proj;
    BonsaiLinearViews o_proj;
    BonsaiRmsNormWeightViews q_norm;
    BonsaiRmsNormWeightViews k_norm;
    uint64_t hidden_size = 0;
    uint64_t attention_heads = 0;
    uint64_t key_value_heads = 0;
    uint64_t head_dimension = 0;
    float scale = 0.0F;
};

struct BonsaiQwenMlpViews {
    BonsaiLinearViews gate_proj;
    BonsaiLinearViews up_proj;
    BonsaiLinearViews down_proj;
    uint64_t hidden_size = 0;
    uint64_t intermediate_size = 0;
};

struct BonsaiQwenLayerViews {
    BonsaiRmsNormWeightViews input_norm;
    BonsaiRmsNormWeightViews post_attention_norm;
    BonsaiQwenAttentionViews attention;
    BonsaiQwenMlpViews mlp;
    uint64_t hidden_size = 0;
};

struct BonsaiQwenTextEncoderViews {
    BonsaiEmbeddingViews embedding;
    BonsaiRmsNormWeightViews final_norm;
    std::vector<BonsaiQwenLayerViews> layers;
    std::vector<uint64_t> hidden_state_layers;
    uint64_t hidden_size = 0;
};

struct BonsaiQwenPromptEmbeddings {
    std::vector<float> values;
    uint64_t batch = 1;
    uint64_t sequence_length = 0;
    uint64_t hidden_size = 0;
    uint64_t selected_layer_count = 0;
};

BonsaiQwenInventorySummary bonsai_require_qwen_text_encoder_tensors(
    const BonsaiSafetensorsIndex& index
);

BonsaiQwenLayerViews bonsai_require_qwen_layer_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    uint64_t layer
);

BonsaiQwenTextEncoderViews bonsai_require_qwen_text_encoder_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index
);

std::vector<float> bonsai_qwen_layer_sequence(
    const BonsaiQwenLayerViews& views,
    const std::vector<float>& hidden_states,
    uint64_t batch,
    uint64_t sequence_length,
    const std::vector<float>& additive_attention_mask
);

BonsaiQwenPromptEmbeddings bonsai_qwen_text_encoder_forward(
    const BonsaiQwenTextEncoderViews& views,
    const std::vector<int32_t>& input_ids,
    const std::vector<int32_t>& attention_mask
);

uint64_t bonsai_qwen_layer_byte_count(const BonsaiQwenLayerViews& views);

uint64_t bonsai_qwen_text_encoder_byte_count(const BonsaiQwenTextEncoderViews& views);

BonsaiQwenLayerProbeSummary bonsai_probe_qwen_text_encoder_layer0(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index
);
