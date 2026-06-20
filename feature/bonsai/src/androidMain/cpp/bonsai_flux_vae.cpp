#include "bonsai_flux_vae.h"

#include "bonsai_tensor.h"

#include <stdexcept>
#include <string>

namespace {

void require_tensor(
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    uint64_t* count
) {
    index.require(key);
    (*count)++;
}

void require_conv(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t expected_input_channels,
    uint64_t expected_output_channels,
    uint64_t* count
) {
    const std::string weight_key = prefix + ".weight";
    const BonsaiTensorDescriptor& weight = index.require(weight_key);
    if (weight.shape.size() != 4) {
        throw std::runtime_error("Bonsai VAE conv weight must be 4D: " + weight_key);
    }
    if (expected_output_channels != 0 && weight.shape[0] != expected_output_channels) {
        throw std::runtime_error("Bonsai VAE conv output channel mismatch: " + weight_key);
    }
    if (expected_input_channels != 0 && weight.shape[1] != expected_input_channels) {
        throw std::runtime_error("Bonsai VAE conv input channel mismatch: " + weight_key);
    }
    (*count)++;

    const BonsaiTensorDescriptor* bias = index.optional(prefix + ".bias");
    if (bias != nullptr) {
        if (bonsai_shape_element_count(bias->shape, bias->key) != weight.shape[0]) {
            throw std::runtime_error("Bonsai VAE conv bias shape mismatch: " + bias->key);
        }
        (*count)++;
    }
}

void require_group_norm(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t expected_channels,
    uint64_t group_count,
    uint64_t* count
) {
    const BonsaiTensorDescriptor& weight = index.require(prefix + ".weight");
    const BonsaiTensorDescriptor& bias = index.require(prefix + ".bias");
    const uint64_t weight_elements = bonsai_shape_element_count(weight.shape, weight.key);
    const uint64_t bias_elements = bonsai_shape_element_count(bias.shape, bias.key);
    if (weight_elements != bias_elements || weight_elements != expected_channels) {
        throw std::runtime_error("Bonsai VAE group norm shape mismatch: " + prefix);
    }
    if (group_count == 0 || expected_channels % group_count != 0) {
        throw std::runtime_error("Bonsai VAE group norm channel/group mismatch: " + prefix);
    }
    *count += 2;
}

uint64_t leading_rows(const BonsaiTensorDescriptor& descriptor) {
    if (descriptor.shape.empty()) {
        throw std::runtime_error("Bonsai VAE linear weight must have dimensions: " + descriptor.key);
    }
    uint64_t rows = 1;
    for (size_t index = 0; index + 1 < descriptor.shape.size(); index++) {
        if (rows != 0 && descriptor.shape[index] > UINT64_MAX / rows) {
            throw std::runtime_error("Bonsai VAE linear shape is too large: " + descriptor.key);
        }
        rows *= descriptor.shape[index];
    }
    return rows;
}

uint64_t trailing_columns(const BonsaiTensorDescriptor& descriptor) {
    if (descriptor.shape.empty()) {
        throw std::runtime_error("Bonsai VAE linear weight must have dimensions: " + descriptor.key);
    }
    return descriptor.shape.back();
}

void require_dense_linear(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    const std::string& fallback_prefix,
    uint64_t expected_input_channels,
    uint64_t expected_output_channels,
    uint64_t* count
) {
    const std::string preferred = prefix + ".weight";
    const std::string fallback = fallback_prefix.empty() ? preferred : fallback_prefix + ".weight";
    const std::string weight_key = index.contains(preferred) ? preferred : fallback;
    const BonsaiTensorDescriptor& weight = index.require(weight_key);
    const uint64_t rows = leading_rows(weight);
    const uint64_t columns = trailing_columns(weight);
    if (rows != expected_output_channels || columns != expected_input_channels) {
        throw std::runtime_error("Bonsai VAE linear shape mismatch: " + weight_key);
    }
    (*count)++;
    const std::string bias_key = weight_key.substr(0, weight_key.size() - std::string(".weight").size()) +
        ".bias";
    const BonsaiTensorDescriptor* bias = index.optional(bias_key);
    if (bias != nullptr) {
        if (bonsai_shape_element_count(bias->shape, bias->key) != rows) {
            throw std::runtime_error("Bonsai VAE linear bias shape mismatch: " + bias->key);
        }
        (*count)++;
    }
}

void require_resnet(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t input_channels,
    uint64_t output_channels,
    uint64_t group_count,
    BonsaiFluxVaeInventorySummary* summary
) {
    require_group_norm(
        index,
        prefix + ".norm1",
        input_channels,
        group_count,
        &summary->logical_tensor_count
    );
    require_conv(
        index,
        prefix + ".conv1",
        input_channels,
        output_channels,
        &summary->logical_tensor_count
    );
    require_group_norm(
        index,
        prefix + ".norm2",
        output_channels,
        group_count,
        &summary->logical_tensor_count
    );
    require_conv(
        index,
        prefix + ".conv2",
        output_channels,
        output_channels,
        &summary->logical_tensor_count
    );
    if (index.optional(prefix + ".conv_shortcut.weight") != nullptr) {
        require_conv(
            index,
            prefix + ".conv_shortcut",
            input_channels,
            output_channels,
            &summary->logical_tensor_count
        );
    }
    summary->resnet_block_count++;
}

void require_attention(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t channels,
    uint64_t group_count,
    BonsaiFluxVaeInventorySummary* summary
) {
    require_group_norm(
        index,
        prefix + ".group_norm",
        channels,
        group_count,
        &summary->logical_tensor_count
    );
    require_dense_linear(index, prefix + ".to_q", "", channels, channels, &summary->logical_tensor_count);
    require_dense_linear(index, prefix + ".to_k", "", channels, channels, &summary->logical_tensor_count);
    require_dense_linear(index, prefix + ".to_v", "", channels, channels, &summary->logical_tensor_count);
    require_dense_linear(
        index,
        prefix + ".to_out.0",
        prefix + ".to_out",
        channels,
        channels,
        &summary->logical_tensor_count
    );
    summary->attention_block_count++;
}

void require_mid_block(
    const BonsaiSafetensorsIndex& index,
    uint64_t channels,
    uint64_t group_count,
    BonsaiFluxVaeInventorySummary* summary
) {
    require_resnet(
        index,
        "decoder.mid_block.resnets.0",
        channels,
        channels,
        group_count,
        summary
    );
    require_attention(index, "decoder.mid_block.attentions.0", channels, group_count, summary);
    require_resnet(
        index,
        "decoder.mid_block.resnets.1",
        channels,
        channels,
        group_count,
        summary
    );
}

void require_up_block(
    const BonsaiSafetensorsIndex& index,
    uint64_t block_index,
    uint64_t input_channels,
    uint64_t output_channels,
    uint64_t layer_count,
    uint64_t group_count,
    bool add_upsample,
    BonsaiFluxVaeInventorySummary* summary
) {
    const std::string prefix = "decoder.up_blocks." + std::to_string(block_index);
    for (uint64_t layer = 0; layer < layer_count; layer++) {
        require_resnet(
            index,
            prefix + ".resnets." + std::to_string(layer),
            layer == 0 ? input_channels : output_channels,
            output_channels,
            group_count,
            summary
        );
    }
    if (add_upsample) {
        require_conv(
            index,
            prefix + ".upsamplers.0.conv",
            output_channels,
            output_channels,
            &summary->logical_tensor_count
        );
    }
    summary->up_block_count++;
}

} // namespace

BonsaiFluxVaeInventorySummary bonsai_require_flux_vae_tensors(
    const BonsaiSafetensorsIndex& index,
    const BonsaiFluxVaeConfig& config
) {
    if (config.block_out_channels_count != 4 ||
        config.block_out_channels.size() != 4 ||
        config.layers_per_block == 0 ||
        config.norm_num_groups == 0) {
        throw std::runtime_error("invalid Flux VAE config for Bonsai native runtime.");
    }
    for (uint64_t channels : config.block_out_channels) {
        if (channels == 0 || channels % config.norm_num_groups != 0) {
            throw std::runtime_error("invalid Flux VAE channel/group config.");
        }
    }

    BonsaiFluxVaeInventorySummary summary;
    require_conv(index, "post_quant_conv", 0, 0, &summary.logical_tensor_count);
    require_tensor(index, "bn.running_mean", &summary.logical_tensor_count);
    require_tensor(index, "bn.running_var", &summary.logical_tensor_count);
    require_conv(index, "decoder.conv_in", 0, config.block_out_channels.back(), &summary.logical_tensor_count);
    require_mid_block(
        index,
        config.block_out_channels.back(),
        config.norm_num_groups,
        &summary
    );

    const uint64_t up_block_layer_count = config.layers_per_block + 1;
    for (uint64_t block = 0; block < config.block_out_channels_count; block++) {
        const uint64_t output_channels =
            config.block_out_channels[static_cast<size_t>(config.block_out_channels_count - 1 - block)];
        const uint64_t input_channels = block == 0
            ? output_channels
            : config.block_out_channels[
                static_cast<size_t>(config.block_out_channels_count - block)
            ];
        require_up_block(
            index,
            block,
            input_channels,
            output_channels,
            up_block_layer_count,
            config.norm_num_groups,
            block + 1 < config.block_out_channels_count,
            &summary
        );
    }

    require_group_norm(
        index,
        "decoder.conv_norm_out",
        config.block_out_channels.front(),
        config.norm_num_groups,
        &summary.logical_tensor_count
    );
    require_conv(index, "decoder.conv_out", config.block_out_channels.front(), 0, &summary.logical_tensor_count);
    return summary;
}
