#pragma once

#include "bonsai_flux_vae.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"
#include "bonsai_vae_ops.h"

#include <cstdint>
#include <vector>

struct BonsaiVaeMidBlockViews {
    BonsaiVaeResnetViews resnet0;
    BonsaiVaeAttentionViews attention;
    BonsaiVaeResnetViews resnet1;
    uint64_t channels = 0;
};

struct BonsaiVaeDecoderViews {
    BonsaiVaeConv2dViews conv_in;
    BonsaiVaeMidBlockViews mid_block;
    std::vector<BonsaiVaeUpBlockViews> up_blocks;
    BonsaiVaeGroupNormViews norm_out;
    BonsaiVaeConv2dViews conv_out;
    uint64_t input_channels = 0;
    uint64_t output_channels = 0;
};

struct BonsaiVaeDecodeViews {
    BonsaiTensorView batch_norm_mean;
    BonsaiTensorView batch_norm_variance;
    BonsaiVaeConv2dViews post_quant_conv;
    BonsaiVaeDecoderViews decoder;
    uint64_t packed_channels = 0;
    float batch_norm_epsilon = 0.0F;
};

BonsaiVaeDecodeViews bonsai_vae_require_decode_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiFluxVaeConfig& config
);

BonsaiNchwTensor bonsai_vae_mid_block_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeMidBlockViews& views
);

BonsaiNchwTensor bonsai_vae_decoder_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeDecoderViews& views
);

BonsaiNchwTensor bonsai_vae_decode_prelude_view_nchw(
    const BonsaiNchwTensor& packed_latents,
    const BonsaiVaeDecodeViews& views
);

BonsaiNchwTensor bonsai_vae_decode_packed_view_nchw(
    const BonsaiNchwTensor& packed_latents,
    const BonsaiVaeDecodeViews& views
);

uint64_t bonsai_vae_mid_block_byte_count(const BonsaiVaeMidBlockViews& views);

uint64_t bonsai_vae_decoder_byte_count(const BonsaiVaeDecoderViews& views);

uint64_t bonsai_vae_decode_byte_count(const BonsaiVaeDecodeViews& views);
