#include "bonsai_vae_ops.h"

#include "bonsai_activation.h"
#include "bonsai_attention.h"
#include "bonsai_linear.h"
#include "bonsai_tensor.h"

#include <algorithm>
#include <cmath>
#include <limits>
#include <stdexcept>
#include <string>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai VAE tensor shape is too large: ") + label);
    }
    return left * right;
}

uint64_t tensor_size(
    uint64_t batch_size,
    uint64_t channels,
    uint64_t height,
    uint64_t width,
    const char* label
) {
    return checked_multiply(
        checked_multiply(checked_multiply(batch_size, channels, label), height, label),
        width,
        label
    );
}

void add_bytes(uint64_t* bytes, uint64_t extra, const char* label) {
    if (*bytes > std::numeric_limits<uint64_t>::max() - extra) {
        throw std::runtime_error(std::string("Bonsai VAE byte count overflow: ") + label);
    }
    *bytes += extra;
}

void require_tensor_shape(const BonsaiNchwTensor& tensor, const char* label) {
    if (tensor.batch_size == 0 || tensor.channels == 0 || tensor.height == 0 || tensor.width == 0) {
        throw std::runtime_error(std::string("Bonsai VAE tensor has invalid shape: ") + label);
    }
    const uint64_t expected = tensor_size(
        tensor.batch_size,
        tensor.channels,
        tensor.height,
        tensor.width,
        label
    );
    if (tensor.values.size() != static_cast<size_t>(expected)) {
        throw std::runtime_error(std::string("Bonsai VAE tensor value count mismatch: ") + label);
    }
}

void require_same_shape(
    const BonsaiNchwTensor& left,
    const BonsaiNchwTensor& right,
    const char* label
) {
    require_tensor_shape(left, label);
    require_tensor_shape(right, label);
    if (left.batch_size != right.batch_size ||
        left.channels != right.channels ||
        left.height != right.height ||
        left.width != right.width) {
        throw std::runtime_error(std::string("Bonsai VAE tensor shape mismatch: ") + label);
    }
}

size_t nchw_index(
    const BonsaiNchwTensor& tensor,
    uint64_t batch,
    uint64_t channel,
    uint64_t row,
    uint64_t column
) {
    return static_cast<size_t>(
        ((batch * tensor.channels + channel) * tensor.height + row) * tensor.width + column
    );
}

std::vector<float> to_attention_layout(const BonsaiNchwTensor& tensor) {
    std::vector<float> output;
    output.reserve(static_cast<size_t>(tensor_size(
        tensor.batch_size,
        tensor.channels,
        tensor.height,
        tensor.width,
        "attention layout"
    )));
    for (uint64_t batch = 0; batch < tensor.batch_size; batch++) {
        for (uint64_t row = 0; row < tensor.height; row++) {
            for (uint64_t column = 0; column < tensor.width; column++) {
                for (uint64_t channel = 0; channel < tensor.channels; channel++) {
                    output.push_back(tensor.values[nchw_index(tensor, batch, channel, row, column)]);
                }
            }
        }
    }
    return output;
}

std::string bias_key_for_weight(const std::string& weight_key) {
    const std::string suffix = ".weight";
    if (weight_key.size() < suffix.size() ||
        weight_key.compare(weight_key.size() - suffix.size(), suffix.size(), suffix) != 0) {
        throw std::runtime_error("Bonsai VAE linear weight key must end with .weight: " + weight_key);
    }
    return weight_key.substr(0, weight_key.size() - suffix.size()) + ".bias";
}

BonsaiLinearViews require_vae_dense_linear_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    const std::string& fallback_prefix,
    uint64_t channels
) {
    if (channels == 0) {
        throw std::runtime_error("Bonsai VAE attention linear channels must be positive: " + prefix);
    }

    const std::string preferred_weight_key = prefix + ".weight";
    const std::string fallback_weight_key = fallback_prefix.empty()
        ? std::string()
        : fallback_prefix + ".weight";
    std::string weight_key = preferred_weight_key;
    if (index.optional(weight_key) == nullptr) {
        if (fallback_weight_key.empty() || index.optional(fallback_weight_key) == nullptr) {
            throw std::runtime_error("missing Bonsai VAE attention linear weight: " + weight_key);
        }
        weight_key = fallback_weight_key;
    }

    BonsaiLinearViews views = bonsai_require_dense_linear_views(
        storage,
        index,
        weight_key,
        bias_key_for_weight(weight_key)
    );
    if (views.input_values != channels || views.output_rows != channels) {
        throw std::runtime_error("Bonsai VAE attention linear shape mismatch: " + weight_key);
    }
    return views;
}

void require_conv_view_shape(
    const BonsaiVaeConv2dViews& views,
    uint64_t input_channels,
    uint64_t output_channels,
    const std::string& prefix
) {
    if (input_channels != 0 && views.input_channels != input_channels) {
        throw std::runtime_error("Bonsai VAE conv input channel mismatch: " + prefix);
    }
    if (output_channels != 0 && views.output_channels != output_channels) {
        throw std::runtime_error("Bonsai VAE conv output channel mismatch: " + prefix);
    }
}

BonsaiNchwTensor from_attention_layout(
    const std::vector<float>& values,
    uint64_t batch_size,
    uint64_t channels,
    uint64_t height,
    uint64_t width
) {
    BonsaiNchwTensor output {
        batch_size,
        channels,
        height,
        width,
        {},
    };
    output.values.assign(
        static_cast<size_t>(tensor_size(batch_size, channels, height, width, "attention output")),
        0.0F
    );
    const uint64_t spatial_length = checked_multiply(height, width, "attention output");
    if (values.size() != static_cast<size_t>(tensor_size(
        batch_size,
        spatial_length,
        channels,
        1,
        "attention output"
    ))) {
        throw std::runtime_error("Bonsai VAE attention output shape mismatch.");
    }
    for (uint64_t batch = 0; batch < batch_size; batch++) {
        for (uint64_t spatial = 0; spatial < spatial_length; spatial++) {
            const uint64_t row = spatial / width;
            const uint64_t column = spatial % width;
            for (uint64_t channel = 0; channel < channels; channel++) {
                const size_t source_index = static_cast<size_t>(
                    (batch * spatial_length + spatial) * channels + channel
                );
                output.values[nchw_index(output, batch, channel, row, column)] =
                    values[source_index];
            }
        }
    }
    return output;
}

size_t weight_index(
    uint64_t input_channels,
    uint64_t kernel_height,
    uint64_t kernel_width,
    uint64_t output_channel,
    uint64_t input_channel,
    uint64_t kernel_row,
    uint64_t kernel_column
) {
    return static_cast<size_t>(
        ((output_channel * input_channels + input_channel) * kernel_height + kernel_row) *
            kernel_width +
        kernel_column
    );
}

} // namespace

BonsaiVaeConv2dViews bonsai_vae_require_conv2d_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix
) {
    const std::string weight_key = prefix + ".weight";
    BonsaiTensorView weight = storage.require_view(index, weight_key);
    if (!bonsai_dtype_is_floating_point(weight.dtype)) {
        throw std::runtime_error("Bonsai VAE conv weight must be floating point: " + weight_key);
    }
    if (weight.descriptor->shape.size() != 4) {
        throw std::runtime_error("Bonsai VAE conv weight must be 4D: " + weight_key);
    }

    BonsaiVaeConv2dViews views {
        weight,
        {},
        false,
        weight.descriptor->shape[0],
        weight.descriptor->shape[1],
        weight.descriptor->shape[2],
        weight.descriptor->shape[3],
        weight.descriptor->shape[2] == 1 ? 0U : 1U,
    };
    if (views.output_channels == 0 ||
        views.input_channels == 0 ||
        views.kernel_height == 0 ||
        views.kernel_width == 0) {
        throw std::runtime_error("Bonsai VAE conv weight shape must be positive: " + weight_key);
    }

    const std::string bias_key = prefix + ".bias";
    const BonsaiTensorDescriptor* bias_descriptor = index.optional(bias_key);
    if (bias_descriptor != nullptr) {
        views.bias = storage.view(*bias_descriptor);
        views.has_bias = true;
        if (!bonsai_dtype_is_floating_point(views.bias.dtype)) {
            throw std::runtime_error("Bonsai VAE conv bias must be floating point: " + bias_key);
        }
        if (views.bias.element_count != views.output_channels) {
            throw std::runtime_error("Bonsai VAE conv bias size mismatch: " + bias_key);
        }
    }
    return views;
}

BonsaiNchwTensor bonsai_vae_conv2d_nchw(
    const BonsaiNchwTensor& input,
    const std::vector<float>& weight,
    uint64_t output_channels,
    uint64_t kernel_height,
    uint64_t kernel_width,
    uint64_t padding,
    const std::vector<float>* bias
) {
    require_tensor_shape(input, "conv input");
    if (output_channels == 0 || kernel_height == 0 || kernel_width == 0) {
        throw std::runtime_error("Bonsai VAE conv dimensions must be positive.");
    }
    const uint64_t expected_weight = tensor_size(
        output_channels,
        input.channels,
        kernel_height,
        kernel_width,
        "conv weight"
    );
    if (weight.size() != static_cast<size_t>(expected_weight)) {
        throw std::runtime_error("Bonsai VAE conv weight size mismatch.");
    }
    if (bias != nullptr && bias->size() != static_cast<size_t>(output_channels)) {
        throw std::runtime_error("Bonsai VAE conv bias size mismatch.");
    }
    if (input.height + 2 * padding < kernel_height || input.width + 2 * padding < kernel_width) {
        throw std::runtime_error("Bonsai VAE conv kernel is larger than padded input.");
    }

    BonsaiNchwTensor output {
        input.batch_size,
        output_channels,
        input.height + 2 * padding - kernel_height + 1,
        input.width + 2 * padding - kernel_width + 1,
        {},
    };
    output.values.assign(
        static_cast<size_t>(
            tensor_size(output.batch_size, output.channels, output.height, output.width, "conv out")
        ),
        0.0F
    );

    const uint64_t input_area = input.height * input.width;
    const uint64_t output_area = output.height * output.width;
    const uint64_t input_batch_stride = input.channels * input_area;
    const uint64_t output_batch_stride = output.channels * output_area;
    const float* input_values = input.values.data();
    const float* weight_values = weight.data();
    float* output_values = output.values.data();

    const int64_t input_height = static_cast<int64_t>(input.height);
    const int64_t input_width = static_cast<int64_t>(input.width);
    const int64_t output_height = static_cast<int64_t>(output.height);
    const int64_t output_width = static_cast<int64_t>(output.width);
    const int64_t pad = static_cast<int64_t>(padding);

    for (uint64_t batch = 0; batch < output.batch_size; batch++) {
        const uint64_t input_batch_offset = batch * input_batch_stride;
        const uint64_t output_batch_offset = batch * output_batch_stride;
        for (uint64_t output_channel = 0; output_channel < output.channels; output_channel++) {
            float* output_plane = output_values + output_batch_offset + output_channel * output_area;
            const float bias_value = bias == nullptr
                ? 0.0F
                : (*bias)[static_cast<size_t>(output_channel)];
            std::fill(output_plane, output_plane + output_area, bias_value);

            for (uint64_t input_channel = 0; input_channel < input.channels; input_channel++) {
                const float* input_plane =
                    input_values + input_batch_offset + input_channel * input_area;
                const float* weight_plane = weight_values + weight_index(
                    input.channels,
                    kernel_height,
                    kernel_width,
                    output_channel,
                    input_channel,
                    0,
                    0
                );

                for (uint64_t kernel_row = 0; kernel_row < kernel_height; kernel_row++) {
                    const int64_t input_row_offset = static_cast<int64_t>(kernel_row) - pad;
                    int64_t output_row_begin = 0;
                    int64_t output_row_end = output_height;
                    if (input_row_offset < 0) {
                        output_row_begin = -input_row_offset;
                    }
                    if (input_row_offset + output_height > input_height) {
                        output_row_end = input_height - input_row_offset;
                    }
                    if (output_row_begin >= output_row_end) {
                        continue;
                    }

                    for (uint64_t kernel_column = 0; kernel_column < kernel_width; kernel_column++) {
                        const int64_t input_column_offset =
                            static_cast<int64_t>(kernel_column) - pad;
                        int64_t output_column_begin = 0;
                        int64_t output_column_end = output_width;
                        if (input_column_offset < 0) {
                            output_column_begin = -input_column_offset;
                        }
                        if (input_column_offset + output_width > input_width) {
                            output_column_end = input_width - input_column_offset;
                        }
                        if (output_column_begin >= output_column_end) {
                            continue;
                        }

                        const float weight_value = weight_plane[
                            static_cast<size_t>(kernel_row * kernel_width + kernel_column)
                        ];
                        for (int64_t output_row = output_row_begin;
                             output_row < output_row_end;
                             output_row++) {
                            const int64_t input_row = output_row + input_row_offset;
                            const int64_t input_column_start =
                                output_column_begin + input_column_offset;
                            const float* input_ptr = input_plane +
                                static_cast<size_t>(input_row) * input.width +
                                static_cast<size_t>(input_column_start);
                            float* output_ptr = output_plane +
                                static_cast<size_t>(output_row) * output.width +
                                static_cast<size_t>(output_column_begin);
                            for (int64_t output_column = output_column_begin;
                                 output_column < output_column_end;
                                 output_column++) {
                                *output_ptr += *input_ptr * weight_value;
                                output_ptr++;
                                input_ptr++;
                            }
                        }
                    }
                }
            }
        }
    }
    return output;
}

BonsaiNchwTensor bonsai_vae_conv2d_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeConv2dViews& views
) {
    require_tensor_shape(input, "conv view input");
    if (input.channels != views.input_channels) {
        throw std::runtime_error("Bonsai VAE conv view input channel mismatch.");
    }
    const std::vector<float> weight = bonsai_tensor_view_to_f32_vector(views.weight);
    const std::vector<float> bias = views.has_bias
        ? bonsai_tensor_view_to_f32_vector(views.bias)
        : std::vector<float> {};
    return bonsai_vae_conv2d_nchw(
        input,
        weight,
        views.output_channels,
        views.kernel_height,
        views.kernel_width,
        views.padding,
        views.has_bias ? &bias : nullptr
    );
}

uint64_t bonsai_vae_conv2d_byte_count(const BonsaiVaeConv2dViews& views) {
    uint64_t bytes = views.weight.byte_count;
    if (views.has_bias) {
        if (bytes > std::numeric_limits<uint64_t>::max() - views.bias.byte_count) {
            throw std::runtime_error("Bonsai VAE conv byte count overflow.");
        }
        bytes += views.bias.byte_count;
    }
    return bytes;
}

BonsaiVaeGroupNormViews bonsai_vae_require_group_norm_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t channels,
    uint64_t group_count,
    float epsilon
) {
    if (channels == 0 || group_count == 0 || channels % group_count != 0) {
        throw std::runtime_error("Bonsai VAE GroupNorm channel/group mismatch: " + prefix);
    }
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai VAE GroupNorm epsilon must be finite and positive: " + prefix);
    }

    const std::string weight_key = prefix + ".weight";
    const std::string bias_key = prefix + ".bias";
    BonsaiTensorView weight = storage.require_view(index, weight_key);
    BonsaiTensorView bias = storage.require_view(index, bias_key);
    if (!bonsai_dtype_is_floating_point(weight.dtype) ||
        !bonsai_dtype_is_floating_point(bias.dtype)) {
        throw std::runtime_error("Bonsai VAE GroupNorm affine tensors must be floating point: " + prefix);
    }
    if (weight.element_count != channels || bias.element_count != channels) {
        throw std::runtime_error("Bonsai VAE GroupNorm affine size mismatch: " + prefix);
    }

    return BonsaiVaeGroupNormViews {
        weight,
        bias,
        channels,
        group_count,
        epsilon,
    };
}

BonsaiNchwTensor bonsai_vae_group_norm_nchw(
    const BonsaiNchwTensor& input,
    uint64_t group_count,
    const std::vector<float>& weight,
    const std::vector<float>& bias,
    float epsilon
) {
    require_tensor_shape(input, "group norm input");
    if (group_count == 0 || input.channels % group_count != 0) {
        throw std::runtime_error("Bonsai VAE group norm channel/group mismatch.");
    }
    if (weight.size() != static_cast<size_t>(input.channels) ||
        bias.size() != static_cast<size_t>(input.channels)) {
        throw std::runtime_error("Bonsai VAE group norm affine size mismatch.");
    }
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai VAE group norm epsilon must be finite and positive.");
    }

    BonsaiNchwTensor output = input;
    output.values.assign(input.values.size(), 0.0F);
    const uint64_t group_size = input.channels / group_count;
    const uint64_t values_per_group = checked_multiply(
        checked_multiply(group_size, input.height, "group norm"),
        input.width,
        "group norm"
    );

    for (uint64_t batch = 0; batch < input.batch_size; batch++) {
        for (uint64_t group = 0; group < group_count; group++) {
            double mean = 0.0;
            for (uint64_t group_channel = 0; group_channel < group_size; group_channel++) {
                const uint64_t channel = group * group_size + group_channel;
                for (uint64_t row = 0; row < input.height; row++) {
                    for (uint64_t column = 0; column < input.width; column++) {
                        mean += static_cast<double>(
                            input.values[nchw_index(input, batch, channel, row, column)]
                        );
                    }
                }
            }
            mean /= static_cast<double>(values_per_group);

            double variance = 0.0;
            for (uint64_t group_channel = 0; group_channel < group_size; group_channel++) {
                const uint64_t channel = group * group_size + group_channel;
                for (uint64_t row = 0; row < input.height; row++) {
                    for (uint64_t column = 0; column < input.width; column++) {
                        const double centered =
                            static_cast<double>(input.values[nchw_index(input, batch, channel, row, column)]) -
                            mean;
                        variance += centered * centered;
                    }
                }
            }
            variance /= static_cast<double>(values_per_group);
            const float scale = 1.0F / std::sqrt(static_cast<float>(variance) + epsilon);

            for (uint64_t group_channel = 0; group_channel < group_size; group_channel++) {
                const uint64_t channel = group * group_size + group_channel;
                for (uint64_t row = 0; row < input.height; row++) {
                    for (uint64_t column = 0; column < input.width; column++) {
                        const size_t index = nchw_index(input, batch, channel, row, column);
                        output.values[index] =
                            (input.values[index] - static_cast<float>(mean)) *
                                scale *
                                weight[static_cast<size_t>(channel)] +
                            bias[static_cast<size_t>(channel)];
                    }
                }
            }
        }
    }
    return output;
}

BonsaiNchwTensor bonsai_vae_group_norm_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeGroupNormViews& views
) {
    require_tensor_shape(input, "group norm view input");
    if (input.channels != views.channels) {
        throw std::runtime_error("Bonsai VAE GroupNorm view input channel mismatch.");
    }
    return bonsai_vae_group_norm_nchw(
        input,
        views.group_count,
        bonsai_tensor_view_to_f32_vector(views.weight),
        bonsai_tensor_view_to_f32_vector(views.bias),
        views.epsilon
    );
}

uint64_t bonsai_vae_group_norm_byte_count(const BonsaiVaeGroupNormViews& views) {
    if (views.weight.byte_count > std::numeric_limits<uint64_t>::max() - views.bias.byte_count) {
        throw std::runtime_error("Bonsai VAE GroupNorm byte count overflow.");
    }
    return views.weight.byte_count + views.bias.byte_count;
}

BonsaiVaeAttentionViews bonsai_vae_require_attention_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t channels,
    uint64_t group_count,
    float epsilon
) {
    if (channels == 0) {
        throw std::runtime_error("Bonsai VAE attention channels must be positive: " + prefix);
    }

    return BonsaiVaeAttentionViews {
        bonsai_vae_require_group_norm_views(
            storage,
            index,
            prefix + ".group_norm",
            channels,
            group_count,
            epsilon
        ),
        require_vae_dense_linear_views(storage, index, prefix + ".to_q", "", channels),
        require_vae_dense_linear_views(storage, index, prefix + ".to_k", "", channels),
        require_vae_dense_linear_views(storage, index, prefix + ".to_v", "", channels),
        require_vae_dense_linear_views(storage, index, prefix + ".to_out.0", prefix + ".to_out", channels),
        channels,
        1.0F / std::sqrt(static_cast<float>(channels)),
    };
}

BonsaiNchwTensor bonsai_vae_attention_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeAttentionViews& views
) {
    require_tensor_shape(input, "attention view input");
    if (input.channels != views.channels ||
        views.group_norm.channels != views.channels ||
        views.to_q.input_values != views.channels ||
        views.to_q.output_rows != views.channels ||
        views.to_k.input_values != views.channels ||
        views.to_k.output_rows != views.channels ||
        views.to_v.input_values != views.channels ||
        views.to_v.output_rows != views.channels ||
        views.to_out.input_values != views.channels ||
        views.to_out.output_rows != views.channels) {
        throw std::runtime_error("Bonsai VAE attention view channel mismatch.");
    }
    if (!std::isfinite(views.scale) || views.scale <= 0.0F) {
        throw std::runtime_error("Bonsai VAE attention view scale must be finite and positive.");
    }

    const BonsaiNchwTensor normed = bonsai_vae_group_norm_view_nchw(input, views.group_norm);
    const uint64_t spatial_length = checked_multiply(input.height, input.width, "attention view");
    const std::vector<float> sequence = to_attention_layout(normed);
    const std::vector<float> query_sequence = bonsai_linear_sequence(
        views.to_q,
        sequence,
        input.batch_size,
        spatial_length
    );
    const std::vector<float> key_sequence = bonsai_linear_sequence(
        views.to_k,
        sequence,
        input.batch_size,
        spatial_length
    );
    const std::vector<float> value_sequence = bonsai_linear_sequence(
        views.to_v,
        sequence,
        input.batch_size,
        spatial_length
    );
    const BonsaiNchwTensor queries = from_attention_layout(
        query_sequence,
        input.batch_size,
        views.channels,
        input.height,
        input.width
    );
    const BonsaiNchwTensor keys = from_attention_layout(
        key_sequence,
        input.batch_size,
        views.channels,
        input.height,
        input.width
    );
    const BonsaiNchwTensor values = from_attention_layout(
        value_sequence,
        input.batch_size,
        views.channels,
        input.height,
        input.width
    );
    const BonsaiNchwTensor attended = bonsai_vae_spatial_attention_nchw(
        queries,
        keys,
        values,
        views.scale
    );
    const std::vector<float> output_sequence = bonsai_linear_sequence(
        views.to_out,
        to_attention_layout(attended),
        input.batch_size,
        spatial_length
    );
    return bonsai_vae_add_nchw(
        input,
        from_attention_layout(
            output_sequence,
            input.batch_size,
            views.channels,
            input.height,
            input.width
        )
    );
}

uint64_t bonsai_vae_attention_byte_count(const BonsaiVaeAttentionViews& views) {
    uint64_t bytes = bonsai_vae_group_norm_byte_count(views.group_norm);
    const std::vector<uint64_t> parts {
        bonsai_linear_byte_count(views.to_q),
        bonsai_linear_byte_count(views.to_k),
        bonsai_linear_byte_count(views.to_v),
        bonsai_linear_byte_count(views.to_out),
    };
    for (uint64_t part : parts) {
        add_bytes(&bytes, part, "attention");
    }
    return bytes;
}

BonsaiVaeResnetViews bonsai_vae_require_resnet_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    uint64_t input_channels,
    uint64_t output_channels,
    uint64_t group_count,
    float epsilon
) {
    if (input_channels == 0 || output_channels == 0) {
        throw std::runtime_error("Bonsai VAE resnet channels must be positive: " + prefix);
    }

    BonsaiVaeResnetViews views {
        bonsai_vae_require_group_norm_views(
            storage,
            index,
            prefix + ".norm1",
            input_channels,
            group_count,
            epsilon
        ),
        bonsai_vae_require_conv2d_views(storage, index, prefix + ".conv1"),
        bonsai_vae_require_group_norm_views(
            storage,
            index,
            prefix + ".norm2",
            output_channels,
            group_count,
            epsilon
        ),
        bonsai_vae_require_conv2d_views(storage, index, prefix + ".conv2"),
        {},
        false,
        input_channels,
        output_channels,
    };
    require_conv_view_shape(views.conv1, input_channels, output_channels, prefix + ".conv1");
    require_conv_view_shape(views.conv2, output_channels, output_channels, prefix + ".conv2");

    const std::string shortcut_weight_key = prefix + ".conv_shortcut.weight";
    if (index.optional(shortcut_weight_key) != nullptr) {
        views.shortcut = bonsai_vae_require_conv2d_views(storage, index, prefix + ".conv_shortcut");
        views.has_shortcut = true;
        require_conv_view_shape(
            views.shortcut,
            input_channels,
            output_channels,
            prefix + ".conv_shortcut"
        );
    } else if (input_channels != output_channels) {
        throw std::runtime_error("Bonsai VAE resnet missing required shortcut: " + prefix);
    }

    return views;
}

BonsaiNchwTensor bonsai_vae_resnet_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeResnetViews& views
) {
    require_tensor_shape(input, "resnet view input");
    if (input.channels != views.input_channels) {
        throw std::runtime_error("Bonsai VAE resnet input channel mismatch.");
    }

    BonsaiNchwTensor output = bonsai_vae_group_norm_view_nchw(input, views.norm1);
    output.values = bonsai_silu(output.values);
    output = bonsai_vae_conv2d_view_nchw(output, views.conv1);
    output = bonsai_vae_group_norm_view_nchw(output, views.norm2);
    output.values = bonsai_silu(output.values);
    output = bonsai_vae_conv2d_view_nchw(output, views.conv2);
    const BonsaiNchwTensor residual = views.has_shortcut
        ? bonsai_vae_conv2d_view_nchw(input, views.shortcut)
        : input;
    return bonsai_vae_add_nchw(output, residual);
}

uint64_t bonsai_vae_resnet_byte_count(const BonsaiVaeResnetViews& views) {
    uint64_t bytes = bonsai_vae_group_norm_byte_count(views.norm1);
    add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.conv1), "resnet conv1");
    add_bytes(&bytes, bonsai_vae_group_norm_byte_count(views.norm2), "resnet norm2");
    add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.conv2), "resnet conv2");
    if (views.has_shortcut) {
        add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.shortcut), "resnet shortcut");
    }
    return bytes;
}

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
) {
    if (input_channels == 0 || output_channels == 0 || layer_count == 0) {
        throw std::runtime_error("Bonsai VAE up block dimensions must be positive: " + prefix);
    }

    BonsaiVaeUpBlockViews views;
    views.input_channels = input_channels;
    views.output_channels = output_channels;
    views.resnets.reserve(static_cast<size_t>(layer_count));
    for (uint64_t layer = 0; layer < layer_count; layer++) {
        views.resnets.push_back(bonsai_vae_require_resnet_views(
            storage,
            index,
            prefix + ".resnets." + std::to_string(layer),
            layer == 0 ? input_channels : output_channels,
            output_channels,
            group_count,
            epsilon
        ));
    }

    if (add_upsample) {
        views.upsample = bonsai_vae_require_conv2d_views(
            storage,
            index,
            prefix + ".upsamplers.0.conv"
        );
        views.has_upsample = true;
        require_conv_view_shape(
            views.upsample,
            output_channels,
            output_channels,
            prefix + ".upsamplers.0.conv"
        );
    }
    return views;
}

BonsaiNchwTensor bonsai_vae_up_block_view_nchw(
    const BonsaiNchwTensor& input,
    const BonsaiVaeUpBlockViews& views
) {
    require_tensor_shape(input, "up block view input");
    if (input.channels != views.input_channels) {
        throw std::runtime_error("Bonsai VAE up block input channel mismatch.");
    }

    BonsaiNchwTensor output = input;
    for (const BonsaiVaeResnetViews& resnet : views.resnets) {
        output = bonsai_vae_resnet_view_nchw(output, resnet);
    }
    if (views.has_upsample) {
        output = bonsai_vae_upsample_nearest2x_nchw(output);
        output = bonsai_vae_conv2d_view_nchw(output, views.upsample);
    }
    return output;
}

uint64_t bonsai_vae_up_block_byte_count(const BonsaiVaeUpBlockViews& views) {
    uint64_t bytes = 0;
    for (const BonsaiVaeResnetViews& resnet : views.resnets) {
        add_bytes(&bytes, bonsai_vae_resnet_byte_count(resnet), "up block resnet");
    }
    if (views.has_upsample) {
        add_bytes(&bytes, bonsai_vae_conv2d_byte_count(views.upsample), "up block upsample");
    }
    return bytes;
}

BonsaiNchwTensor bonsai_vae_spatial_attention_nchw(
    const BonsaiNchwTensor& queries,
    const BonsaiNchwTensor& keys,
    const BonsaiNchwTensor& values,
    float scale
) {
    require_same_shape(queries, keys, "attention key");
    require_same_shape(queries, values, "attention value");
    if (!std::isfinite(scale) || scale <= 0.0F) {
        throw std::runtime_error("Bonsai VAE attention scale must be finite and positive.");
    }

    const uint64_t spatial_length = checked_multiply(
        queries.height,
        queries.width,
        "attention"
    );
    const std::vector<float> output = bonsai_scaled_dot_product_attention(
        to_attention_layout(queries),
        to_attention_layout(keys),
        to_attention_layout(values),
        {},
        queries.batch_size,
        spatial_length,
        queries.channels,
        scale
    );
    return from_attention_layout(
        output,
        queries.batch_size,
        queries.channels,
        queries.height,
        queries.width
    );
}

BonsaiNchwTensor bonsai_vae_add_nchw(
    const BonsaiNchwTensor& left,
    const BonsaiNchwTensor& right
) {
    require_same_shape(left, right, "add");
    BonsaiNchwTensor output = left;
    for (size_t index = 0; index < output.values.size(); index++) {
        output.values[index] = left.values[index] + right.values[index];
    }
    return output;
}

BonsaiNchwTensor bonsai_vae_upsample_nearest2x_nchw(
    const BonsaiNchwTensor& input
) {
    require_tensor_shape(input, "upsample input");
    BonsaiNchwTensor output {
        input.batch_size,
        input.channels,
        input.height * 2,
        input.width * 2,
        {},
    };
    output.values.assign(
        static_cast<size_t>(
            tensor_size(output.batch_size, output.channels, output.height, output.width, "upsample")
        ),
        0.0F
    );

    for (uint64_t batch = 0; batch < input.batch_size; batch++) {
        for (uint64_t channel = 0; channel < input.channels; channel++) {
            for (uint64_t row = 0; row < output.height; row++) {
                for (uint64_t column = 0; column < output.width; column++) {
                    output.values[nchw_index(output, batch, channel, row, column)] =
                        input.values[nchw_index(input, batch, channel, row / 2, column / 2)];
                }
            }
        }
    }
    return output;
}

BonsaiNchwTensor bonsai_vae_denormalize_channels_nchw(
    const BonsaiNchwTensor& input,
    const std::vector<float>& mean,
    const std::vector<float>& variance,
    float epsilon
) {
    require_tensor_shape(input, "denormalize input");
    if (mean.size() != static_cast<size_t>(input.channels) ||
        variance.size() != static_cast<size_t>(input.channels)) {
        throw std::runtime_error("Bonsai VAE denormalize channel size mismatch.");
    }
    if (epsilon <= 0.0F || !std::isfinite(epsilon)) {
        throw std::runtime_error("Bonsai VAE denormalize epsilon must be finite and positive.");
    }

    BonsaiNchwTensor output = input;
    for (uint64_t batch = 0; batch < input.batch_size; batch++) {
        for (uint64_t channel = 0; channel < input.channels; channel++) {
            const float stddev = std::sqrt(variance[static_cast<size_t>(channel)] + epsilon);
            const float offset = mean[static_cast<size_t>(channel)];
            for (uint64_t row = 0; row < input.height; row++) {
                for (uint64_t column = 0; column < input.width; column++) {
                    const size_t index = nchw_index(input, batch, channel, row, column);
                    output.values[index] = input.values[index] * stddev + offset;
                }
            }
        }
    }
    return output;
}

BonsaiNchwTensor bonsai_vae_unpatchify_nchw(
    const BonsaiNchwTensor& input
) {
    require_tensor_shape(input, "unpatchify input");
    if (input.channels % 4 != 0) {
        throw std::runtime_error("Bonsai VAE unpatchify channels must be divisible by 4.");
    }

    BonsaiNchwTensor output {
        input.batch_size,
        input.channels / 4,
        input.height * 2,
        input.width * 2,
        {},
    };
    output.values.assign(
        static_cast<size_t>(
            tensor_size(
                output.batch_size,
                output.channels,
                output.height,
                output.width,
                "unpatchify"
            )
        ),
        0.0F
    );

    for (uint64_t batch = 0; batch < input.batch_size; batch++) {
        for (uint64_t channel = 0; channel < output.channels; channel++) {
            for (uint64_t patch_row = 0; patch_row < 2; patch_row++) {
                for (uint64_t patch_column = 0; patch_column < 2; patch_column++) {
                    const uint64_t input_channel = ((channel * 2) + patch_row) * 2 + patch_column;
                    for (uint64_t row = 0; row < input.height; row++) {
                        for (uint64_t column = 0; column < input.width; column++) {
                            output.values[nchw_index(
                                output,
                                batch,
                                channel,
                                row * 2 + patch_row,
                                column * 2 + patch_column
                            )] = input.values[nchw_index(
                                input,
                                batch,
                                input_channel,
                                row,
                                column
                            )];
                        }
                    }
                }
            }
        }
    }
    return output;
}
