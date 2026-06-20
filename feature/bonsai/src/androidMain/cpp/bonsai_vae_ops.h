#pragma once

#include "bonsai_linear.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <string>
#include <vector>

struct BonsaiNchwTensor {
    uint64_t batch_size = 0;
    uint64_t channels = 0;
    uint64_t height = 0;
    uint64_t width = 0;
    std::vector<float> values;
};

struct BonsaiVaeConv2dViews {
    BonsaiTensorView weight;
    BonsaiTensorView bias;
    bool has_bias = false;
    uint64_t output_channels = 0;
    uint64_t input_channels = 0;
    uint64_t kernel_height = 0;
    uint64_t kernel_width = 0;
    uint64_t padding = 0;
};

struct BonsaiVaeGroupNormViews {
    BonsaiTensorView weight;
    BonsaiTensorView bias;
    uint64_t channels = 0;
    uint64_t group_count = 0;
    float epsilon = 1e-6F;
};

struct BonsaiVaeAttentionViews {
    BonsaiVaeGroupNormViews group_norm;
    BonsaiLinearViews to_q;
    BonsaiLinearViews to_k;
    BonsaiLinearViews to_v;
    BonsaiLinearViews to_out;
    uint64_t channels = 0;
    float scale = 0.0F;
};

struct BonsaiVaeResnetViews {
    BonsaiVaeGroupNormViews norm1;
    BonsaiVaeConv2dViews conv1;
    BonsaiVaeGroupNormViews norm2;
    BonsaiVaeConv2dViews conv2;
    BonsaiVaeConv2dViews shortcut;
    bool has_shortcut = false;
    uint64_t input_channels = 0;
    uint64_t output_channels = 0;
};

struct BonsaiVaeUpBlockViews {
    std::vector<BonsaiVaeResnetViews> resnets;
    BonsaiVaeConv2dViews upsample;
    bool has_upsample = false;
    uint64_t input_channels = 0;
    uint64_t output_channels = 0;
};

BonsaiVaeConv2dViews bonsai_vae_require_conv2d_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix
);

BonsaiNchwTensor bonsai_vae_conv2d_nchw(
    const BonsaiNchwTensor& input,
    const std::vector<float>& weight,
    uint64_t output_channels,
    uint64_t kernel_height,
    uint64_t kernel_width,
    uint64_t padding,
    const std::vector<float>* bias
);

BonsaiNchwTensor bonsai_vae_conv2d_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeConv2dViews& views
);

uint64_t bonsai_vae_conv2d_byte_count(const BonsaiVaeConv2dViews& views);

BonsaiVaeGroupNormViews bonsai_vae_require_group_norm_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t channels,
    uint64_t group_count,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_group_norm_nchw(
    const BonsaiNchwTensor& input,
    uint64_t group_count,
    const std::vector<float>& weight,
    const std::vector<float>& bias,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_group_norm_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeGroupNormViews& views
);

uint64_t bonsai_vae_group_norm_byte_count(const BonsaiVaeGroupNormViews& views);

BonsaiVaeAttentionViews bonsai_vae_require_attention_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t channels,
    uint64_t group_count,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_attention_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeAttentionViews& views
);

uint64_t bonsai_vae_attention_byte_count(const BonsaiVaeAttentionViews& views);

BonsaiVaeResnetViews bonsai_vae_require_resnet_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t input_channels,
    uint64_t output_channels,
    uint64_t group_count,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_resnet_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeResnetViews& views
);

uint64_t bonsai_vae_resnet_byte_count(const BonsaiVaeResnetViews& views);

BonsaiVaeUpBlockViews bonsai_vae_require_up_block_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t input_channels,
    uint64_t output_channels,
    uint64_t layer_count,
    uint64_t group_count,
    bool add_upsample,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_up_block_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeUpBlockViews& views
);

uint64_t bonsai_vae_up_block_byte_count(const BonsaiVaeUpBlockViews& views);

BonsaiNchwTensor bonsai_vae_spatial_attention_nchw(
    const BonsaiNchwTensor& queries,
    const BonsaiNchwTensor& keys,
    const BonsaiNchwTensor& values,
    float scale
);

BonsaiNchwTensor bonsai_vae_add_nchw(
    const BonsaiNchwTensor& left,
    const BonsaiNchwTensor& right
);

BonsaiNchwTensor bonsai_vae_upsample_nearest2x_nchw(
    const BonsaiNchwTensor& input
);

BonsaiNchwTensor bonsai_vae_denormalize_channels_nchw(
    const BonsaiNchwTensor& input,
    const std::vector<float>& mean,
    const std::vector<float>& variance,
    float epsilon
);

BonsaiNchwTensor bonsai_vae_unpatchify_nchw(
    const BonsaiNchwTensor& input
);
