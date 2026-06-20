#include "bonsai_vae_decoder.h"

#include "bonsai_activation.h"
#include "bonsai_tensor.h"

#include <android/log.h>

#include <limits>
#include <stdexcept>
#include <string>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";

void log_vae_phase(const char* phase, const BonsaiNchwTensor& tensor) {
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=%s batch=%llu channels=%llu height=%llu width=%llu",
        phase,
        static_cast<unsigned long long>(tensor.batch_size),
        static_cast<unsigned long long>(tensor.channels),
        static_cast<unsigned long long>(tensor.height),
        static_cast<unsigned long long>(tensor.width)
    );
}

void log_vae_block_phase(const char* phase, uint64_t block, const BonsaiNchwTensor& tensor) {
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=%s block=%llu batch=%llu channels=%llu height=%llu width=%llu",
        phase,
        static_cast<unsigned long long>(block),
        static_cast<unsigned long long>(tensor.batch_size),
        static_cast<unsigned long long>(tensor.channels),
        static_cast<unsigned long long>(tensor.height),
        static_cast<unsigned long long>(tensor.width)
    );
}

void add_bytes(uint64_t* bytes, uint64_t extra, const char* label) {
    if (*bytes > std::numeric_limits<uint64_t>::max() - extra) {
        throw std::runtime_error(std::string("Bonsai VAE decoder byte count overflow: ") + label);
    }
    *bytes += extra;
}

void require_conv_shape(
    const BonsaiVaeConv2dViews& views,
    uint64_t input_channels,
    uint64_t output_channels,
    const std::string& prefix
) {
    if (input_channels != 0 && views.input_channels != input_channels) {
        throw std::runtime_error("Bonsai VAE decoder conv input channel mismatch: " + prefix);
    }
    if (output_channels != 0 && views.output_channels != output_channels) {
        throw std::runtime_error("Bonsai VAE decoder conv output channel mismatch: " + prefix);
    }
}

void require_tensor_vector(
    const BonsaiTensorView& view,
    const std::string& key
) {
    if (!bonsai_dtype_is_floating_point(view.dtype)) {
        throw std::runtime_error("Bonsai VAE decoder tensor must be floating point: " + key);
    }
    if (view.element_count == 0) {
        throw std::runtime_error("Bonsai VAE decoder tensor must be non-empty: " + key);
    }
}

BonsaiVaeMidBlockViews require_mid_block_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    uint64_t channels,
    uint64_t group_count,
    float epsilon
) {
    if (channels == 0) {
        throw std::runtime_error("Bonsai VAE mid block channels must be positive.");
    }
    return BonsaiVaeMidBlockViews {
        bonsai_vae_require_resnet_views(
            storage,
            index,
            "decoder.mid_block.resnets.0",
            channels,
            channels,
            group_count,
            epsilon
        ),
        bonsai_vae_require_attention_views(
            storage,
            index,
            "decoder.mid_block.attentions.0",
            channels,
            group_count,
            epsilon
        ),
        bonsai_vae_require_resnet_views(
            storage,
            index,
            "decoder.mid_block.resnets.1",
            channels,
            channels,
            group_count,
            epsilon
        ),
        channels,
    };
}

BonsaiVaeDecoderViews require_decoder_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiFluxVaeConfig& config,
    uint64_t decoder_input_channels
) {
    if (config.block_out_channels_count == 0 ||
        config.block_out_channels.size() != static_cast<size_t>(config.block_out_channels_count) ||
        config.layers_per_block == 0 ||
        config.norm_num_groups == 0) {
        throw std::runtime_error("invalid Bonsai VAE decoder config.");
    }

    BonsaiVaeDecoderViews views;
    views.input_channels = decoder_input_channels;
    views.conv_in = bonsai_vae_require_conv2d_views(storage, index, "decoder.conv_in");
    require_conv_shape(
        views.conv_in,
        decoder_input_channels,
        config.block_out_channels.back(),
        "decoder.conv_in"
    );
    views.mid_block = require_mid_block_views(
        storage,
        index,
        config.block_out_channels.back(),
        config.norm_num_groups,
        1e-6F
    );

    views.up_blocks.reserve(static_cast<size_t>(config.block_out_channels_count));
    const uint64_t layer_count = config.layers_per_block + 1U;
    for (uint64_t block = 0; block < config.block_out_channels_count; block++) {
        const uint64_t output_channels = config.block_out_channels[
            static_cast<size_t>(config.block_out_channels_count - 1U - block)
        ];
        const uint64_t input_channels = block == 0
            ? output_channels
            : config.block_out_channels[static_cast<size_t>(config.block_out_channels_count - block)];
        views.up_blocks.push_back(bonsai_vae_require_up_block_views(
            storage,
            index,
            "decoder.up_blocks." + std::to_string(block),
            input_channels,
            output_channels,
            layer_count,
            config.norm_num_groups,
            block + 1U < config.block_out_channels_count,
            1e-6F
        ));
    }

    views.norm_out = bonsai_vae_require_group_norm_views(
        storage,
        index,
        "decoder.conv_norm_out",
        config.block_out_channels.front(),
        config.norm_num_groups,
        1e-6F
    );
    views.conv_out = bonsai_vae_require_conv2d_views(storage, index, "decoder.conv_out");
    require_conv_shape(
        views.conv_out,
        config.block_out_channels.front(),
        0,
        "decoder.conv_out"
    );
    views.output_channels = views.conv_out.output_channels;
    return views;
}

} // namespace

BonsaiVaeDecodeViews bonsai_vae_require_decode_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiFluxVaeConfig& config
) {
    BonsaiTensorView mean = storage.require_view(index, "bn.running_mean");
    BonsaiTensorView variance = storage.require_view(index, "bn.running_var");
    require_tensor_vector(mean, "bn.running_mean");
    require_tensor_vector(variance, "bn.running_var");
    if (mean.element_count != variance.element_count || mean.element_count % 4U != 0) {
        throw std::runtime_error("Bonsai VAE decoder batch norm channel mismatch.");
    }
    if (config.batch_norm_eps <= 0.0F) {
        throw std::runtime_error("Bonsai VAE decoder batch norm epsilon must be positive.");
    }

    BonsaiVaeConv2dViews post_quant_conv = bonsai_vae_require_conv2d_views(
        storage,
        index,
        "post_quant_conv"
    );
    require_conv_shape(
        post_quant_conv,
        mean.element_count / 4U,
        0,
        "post_quant_conv"
    );

    return BonsaiVaeDecodeViews {
        mean,
        variance,
        post_quant_conv,
        require_decoder_views(storage, index, config, post_quant_conv.output_channels),
        mean.element_count,
        config.batch_norm_eps,
    };
}

BonsaiNchwTensor bonsai_vae_mid_block_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeMidBlockViews& views
) {
    if (input.channels != views.channels) {
        throw std::runtime_error("Bonsai VAE mid block input channel mismatch.");
    }
    log_vae_phase("vae_mid_resnet0_start", input);
    BonsaiNchwTensor output = bonsai_vae_resnet_view_nchw(input, views.resnet0);
    log_vae_phase("vae_mid_resnet0_done", output);
    log_vae_phase("vae_mid_attention_start", output);
    output = bonsai_vae_attention_view_nchw(output, views.attention);
    log_vae_phase("vae_mid_attention_done", output);
    log_vae_phase("vae_mid_resnet1_start", output);
    output = bonsai_vae_resnet_view_nchw(output, views.resnet1);
    log_vae_phase("vae_mid_resnet1_done", output);
    return output;
}

BonsaiNchwTensor bonsai_vae_decoder_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeDecoderViews& views
) {
    if (input.channels != views.input_channels) {
        throw std::runtime_error("Bonsai VAE decoder input channel mismatch.");
    }

    log_vae_phase("vae_decoder_conv_in_start", input);
    BonsaiNchwTensor output = bonsai_vae_conv2d_view_nchw(input, views.conv_in);
    log_vae_phase("vae_decoder_conv_in_done", output);
    log_vae_phase("vae_decoder_mid_start", output);
    output = bonsai_vae_mid_block_view_nchw(output, views.mid_block);
    log_vae_phase("vae_decoder_mid_done", output);
    for (size_t index = 0; index < views.up_blocks.size(); index++) {
        const BonsaiVaeUpBlockViews& up_block = views.up_blocks[index];
        log_vae_block_phase("vae_decoder_up_block_start", index + 1U, output);
        output = bonsai_vae_up_block_view_nchw(output, up_block);
        log_vae_block_phase("vae_decoder_up_block_done", index + 1U, output);
    }
    log_vae_phase("vae_decoder_norm_out_start", output);
    output = bonsai_vae_group_norm_view_nchw(output, views.norm_out);
    log_vae_phase("vae_decoder_norm_out_done", output);
    output.values = bonsai_silu(output.values);
    log_vae_phase("vae_decoder_conv_out_start", output);
    return bonsai_vae_conv2d_view_nchw(output, views.conv_out);
}

BonsaiNchwTensor bonsai_vae_decode_prelude_view_nchw(
    const BonsaiNchwTensor& packed_latents,
    const BonsaiVaeDecodeViews& views
) {
    if (packed_latents.channels != views.packed_channels) {
        throw std::runtime_error("Bonsai VAE packed latent channel mismatch.");
    }
    log_vae_phase("vae_prelude_denormalize_start", packed_latents);
    const BonsaiNchwTensor denormalized = bonsai_vae_denormalize_channels_nchw(
        packed_latents,
        bonsai_tensor_view_to_f32_vector(views.batch_norm_mean),
        bonsai_tensor_view_to_f32_vector(views.batch_norm_variance),
        views.batch_norm_epsilon
    );
    log_vae_phase("vae_prelude_denormalize_done", denormalized);
    log_vae_phase("vae_prelude_unpatchify_start", denormalized);
    const BonsaiNchwTensor unpatchified = bonsai_vae_unpatchify_nchw(denormalized);
    log_vae_phase("vae_prelude_unpatchify_done", unpatchified);
    log_vae_phase("vae_prelude_post_quant_conv_start", unpatchified);
    return bonsai_vae_conv2d_view_nchw(
        unpatchified,
        views.post_quant_conv
    );
}

BonsaiNchwTensor bonsai_vae_decode_packed_view_nchw(
    const BonsaiNchwTensor& packed_latents,
    const BonsaiVaeDecodeViews& views
) {
    BonsaiNchwTensor output = bonsai_vae_decode_prelude_view_nchw(packed_latents, views);
    log_vae_phase("vae_prelude_done", output);
    output = bonsai_vae_decoder_view_nchw(output, views.decoder);
    log_vae_phase("vae_decoder_done", output);
    return output;
}

uint64_t bonsai_vae_mid_block_byte_count(const BonsaiVaeMidBlockViews& views) {
    uint64_t bytes = bonsai_vae_resnet_byte_count(views.resnet0);
    add_bytes(&bytes, bonsai_vae_attention_byte_count(views.attention), "mid attention");
    add_bytes(&bytes, bonsai_vae_resnet_byte_count(views.resnet1), "mid resnet1");
    return bytes;
}

uint64_t bonsai_vae_decoder_byte_count(const BonsaiVaeDecoderViews& views) {
    uint64_t bytes = bonsai_vae_conv2d_byte_count(views.conv_in);
    add_bytes(&bytes, bonsai_vae_mid_block_byte_count(views.mid_block), "mid block");
    for (const BonsaiVaeUpBlockViews& up_block : views.up_blocks) {
        add_bytes(&bytes, bonsai_vae_up_block_byte_count(up_block), "up block");
    }
    add_bytes(&bytes, bonsai_vae_group_norm_byte_count(views.norm_out), "norm out");
    add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.conv_out), "conv out");
    return bytes;
}

uint64_t bonsai_vae_decode_byte_count(const BonsaiVaeDecodeViews& views) {
    uint64_t bytes = views.batch_norm_mean.byte_count;
    add_bytes(&bytes, views.batch_norm_variance.byte_count, "batch norm variance");
    add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.post_quant_conv), "post quant conv");
    add_bytes(&bytes, bonsai_vae_decoder_byte_count(views.decoder), "decoder");
    return bytes;
}
