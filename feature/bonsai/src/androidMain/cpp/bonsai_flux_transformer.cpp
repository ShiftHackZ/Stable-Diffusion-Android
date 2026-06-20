#include "bonsai_flux_transformer.h"

#include "bonsai_activation.h"
#include "bonsai_attention.h"
#include "bonsai_flux_attention_layout.h"
#include "bonsai_flux_modulation.h"
#include "bonsai_flux_output.h"
#include "bonsai_flux_pos_embed.h"
#include "bonsai_flux_rope.h"
#include "bonsai_flux_time_embedding.h"
#include "bonsai_linear.h"
#include "bonsai_norm.h"
#include "bonsai_tensor.h"

#include <android/log.h>

#include <cmath>
#include <limits>
#include <stdexcept>
#include <string>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";
constexpr uint64_t FLUX_DOUBLE_BLOCKS = 5;
constexpr uint64_t FLUX_SINGLE_BLOCKS = 20;
constexpr uint64_t FLUX_DIM = 3072;
constexpr uint64_t FLUX_TEXT_HIDDEN = 7680;
constexpr uint64_t FLUX_LATENT_CHANNELS = 128;
constexpr uint64_t FLUX_TIME_EMBED = 256;
constexpr uint64_t FLUX_MLP_HIDDEN = 9216;

uint64_t checked_multiply(uint64_t left, uint64_t right, const std::string& key) {
    if (left != 0 && right > UINT64_MAX / left) {
        throw std::runtime_error("Bonsai Flux tensor shape is too large: " + key);
    }
    return left * right;
}

void log_flux_block_phase(
    const char* phase,
    uint64_t block,
    uint64_t text_sequence_length,
    uint64_t image_sequence_length
) {
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=%s block=%llu text=%llu image=%llu",
        phase,
        static_cast<unsigned long long>(block),
        static_cast<unsigned long long>(text_sequence_length),
        static_cast<unsigned long long>(image_sequence_length)
    );
}

uint64_t leading_rows(const BonsaiTensorDescriptor& descriptor) {
    if (descriptor.shape.empty()) {
        throw std::runtime_error("Bonsai Flux tensor must have dimensions: " + descriptor.key);
    }
    uint64_t rows = 1;
    for (size_t index = 0; index + 1 < descriptor.shape.size(); index++) {
        rows = checked_multiply(rows, descriptor.shape[index], descriptor.key);
    }
    return rows;
}

uint64_t trailing_columns(const BonsaiTensorDescriptor& descriptor) {
    if (descriptor.shape.empty()) {
        throw std::runtime_error("Bonsai Flux tensor must have dimensions: " + descriptor.key);
    }
    return descriptor.shape.back();
}

void require_dense_linear(
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    uint64_t expected_input,
    uint64_t expected_output,
    uint64_t* count
) {
    const BonsaiTensorDescriptor& descriptor = index.require(key);
    if (leading_rows(descriptor) != expected_output ||
        trailing_columns(descriptor) != expected_input) {
        throw std::runtime_error("Bonsai Flux dense linear shape mismatch: " + key);
    }
    (*count)++;
}

void require_norm(
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    uint64_t expected_elements,
    uint64_t* count
) {
    const BonsaiTensorDescriptor& descriptor = index.require(key);
    if (bonsai_shape_element_count(descriptor.shape, key) != expected_elements) {
        throw std::runtime_error("Bonsai Flux norm shape mismatch: " + key);
    }
    (*count)++;
}

void require_packed_linear(
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    int bits,
    int group_size,
    uint64_t expected_input,
    uint64_t expected_output,
    uint64_t* count
) {
    const BonsaiPackedWeightDescriptor descriptor = index.require_packed_weight(
        key,
        bits,
        group_size
    );
    if (!descriptor.packed) {
        const BonsaiTensorDescriptor& dense = index.require(key);
        if (leading_rows(dense) != expected_output || trailing_columns(dense) != expected_input) {
            throw std::runtime_error("Bonsai Flux dense fallback shape mismatch: " + key);
        }
    } else {
        const BonsaiTensorDescriptor& scales = index.require(descriptor.scales_key);
        if (leading_rows(scales) != expected_output ||
            checked_multiply(
                trailing_columns(scales),
                static_cast<uint64_t>(group_size),
                scales.key
            ) != expected_input) {
            throw std::runtime_error("Bonsai Flux packed linear shape mismatch: " + key);
        }
    }
    (*count)++;
}

BonsaiLinearViews require_dense_linear_view_checked(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    uint64_t expected_input,
    uint64_t expected_output
) {
    BonsaiLinearViews views = bonsai_require_dense_linear_views(
        storage,
        index,
        key,
        key.substr(0, key.size() - std::string(".weight").size()) + ".bias"
    );
    if (views.input_values != expected_input || views.output_rows != expected_output) {
        throw std::runtime_error("Bonsai Flux dense linear view shape mismatch: " + key);
    }
    return views;
}

BonsaiLinearViews require_packed_linear_view_checked(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    int bits,
    int group_size,
    uint64_t expected_input,
    uint64_t expected_output
) {
    BonsaiLinearViews views = bonsai_require_packed_linear_views(
        storage,
        index,
        index.require_packed_weight(key, bits, group_size),
        key.substr(0, key.size() - std::string(".weight").size()) + ".bias"
    );
    if (views.input_values != expected_input || views.output_rows != expected_output) {
        throw std::runtime_error("Bonsai Flux packed linear view shape mismatch: " + key);
    }
    return views;
}

BonsaiRmsNormWeightViews require_norm_view_checked(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& key,
    uint64_t expected_elements
) {
    BonsaiRmsNormWeightViews views = bonsai_require_rms_norm_weight(storage, index, key);
    if (views.dimensions != expected_elements) {
        throw std::runtime_error("Bonsai Flux norm view shape mismatch: " + key);
    }
    return views;
}

void add_bytes(uint64_t* bytes, uint64_t extra, const char* label) {
    if (*bytes > std::numeric_limits<uint64_t>::max() - extra) {
        throw std::runtime_error(std::string("Bonsai Flux byte count overflow: ") + label);
    }
    *bytes += extra;
}

uint64_t checked_add(uint64_t left, uint64_t right, const std::string& key) {
    if (left > std::numeric_limits<uint64_t>::max() - right) {
        throw std::runtime_error("Bonsai Flux tensor shape is too large: " + key);
    }
    return left + right;
}

size_t checked_size(uint64_t value, const std::string& key) {
    if (value > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error("Bonsai Flux tensor shape is too large: " + key);
    }
    return static_cast<size_t>(value);
}

size_t checked_size_3(uint64_t first, uint64_t second, uint64_t third, const std::string& key) {
    return checked_size(
        checked_multiply(checked_multiply(first, second, key), third, key),
        key
    );
}

void require_sequence_shape(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    const std::string& key
) {
    if (values.size() != checked_size_3(batch, sequence_length, dimensions, key)) {
        throw std::runtime_error("Bonsai Flux sequence shape mismatch: " + key);
    }
}

std::vector<float> norm_weight_values(const BonsaiRmsNormWeightViews& views) {
    return bonsai_tensor_view_to_f32_vector(views.weight);
}

std::vector<float> linear_batch_vector(
    const BonsaiLinearViews& views,
    const std::vector<float>& input,
    uint64_t batch
) {
    return bonsai_linear_sequence(views, input, batch, 1);
}

std::vector<float> concat_token_sequences(
    const std::vector<float>& first,
    const std::vector<float>& second,
    uint64_t batch,
    uint64_t first_sequence_length,
    uint64_t second_sequence_length,
    uint64_t dimensions
) {
    require_sequence_shape(first, batch, first_sequence_length, dimensions, "concat first");
    require_sequence_shape(second, batch, second_sequence_length, dimensions, "concat second");
    const uint64_t output_sequence_length = checked_add(
        first_sequence_length,
        second_sequence_length,
        "concat sequence"
    );
    std::vector<float> output(
        checked_size_3(batch, output_sequence_length, dimensions, "concat output"),
        0.0F
    );
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < first_sequence_length; token++) {
            for (uint64_t column = 0; column < dimensions; column++) {
                output[static_cast<size_t>(
                    (batch_index * output_sequence_length + token) * dimensions + column
                )] = first[static_cast<size_t>(
                    (batch_index * first_sequence_length + token) * dimensions + column
                )];
            }
        }
        for (uint64_t token = 0; token < second_sequence_length; token++) {
            for (uint64_t column = 0; column < dimensions; column++) {
                output[static_cast<size_t>(
                    (
                        batch_index * output_sequence_length +
                        first_sequence_length +
                        token
                    ) * dimensions + column
                )] = second[static_cast<size_t>(
                    (batch_index * second_sequence_length + token) * dimensions + column
                )];
            }
        }
    }
    return output;
}

std::vector<float> slice_token_sequence(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    uint64_t start_token,
    uint64_t token_count
) {
    require_sequence_shape(input, batch, sequence_length, dimensions, "slice input");
    if (start_token > sequence_length || token_count > sequence_length - start_token) {
        throw std::runtime_error("Bonsai Flux token slice is out of range.");
    }
    std::vector<float> output(
        checked_size_3(batch, token_count, dimensions, "slice output"),
        0.0F
    );
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t token = 0; token < token_count; token++) {
            for (uint64_t column = 0; column < dimensions; column++) {
                output[static_cast<size_t>(
                    (batch_index * token_count + token) * dimensions + column
                )] = input[static_cast<size_t>(
                    (batch_index * sequence_length + start_token + token) * dimensions + column
                )];
            }
        }
    }
    return output;
}

std::vector<float> concat_rotary_rows(
    const BonsaiFluxRotaryEmbedding& first,
    const BonsaiFluxRotaryEmbedding& second,
    bool cosine
) {
    if (first.dimensions != second.dimensions || first.dimensions == 0) {
        throw std::runtime_error("Bonsai Flux rotary concat dimension mismatch.");
    }
    const std::vector<float>& first_values = cosine ? first.cos : first.sin;
    const std::vector<float>& second_values = cosine ? second.cos : second.sin;
    std::vector<float> output;
    output.reserve(first_values.size() + second_values.size());
    output.insert(output.end(), first_values.begin(), first_values.end());
    output.insert(output.end(), second_values.begin(), second_values.end());
    return output;
}

std::vector<float> flux_apply_projection_rope(
    const std::vector<float>& projection,
    const BonsaiRmsNormWeightViews& norm,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    return bonsai_flux_apply_rms_norm_and_rope(
        bonsai_flux_sequence_to_heads(projection, batch, sequence_length, heads, head_dimension),
        norm_weight_values(norm),
        cos_values,
        sin_values,
        checked_multiply(batch, heads, "flux rope batch heads"),
        sequence_length,
        head_dimension,
        1e-5F
    );
}

std::vector<float> flux_swiglu_projection(
    const BonsaiLinearViews& input_projection,
    const BonsaiLinearViews& output_projection,
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t mlp_hidden_dimensions
) {
    return bonsai_linear_sequence(
        output_projection,
        bonsai_swiglu_last_dimension(
            bonsai_linear_sequence(input_projection, input, batch, sequence_length),
            checked_multiply(mlp_hidden_dimensions, 2U, "flux swiglu width")
        ),
        batch,
        sequence_length
    );
}

void apply_flux_double_block(
    const BonsaiFluxDoubleBlockViews& block,
    const std::vector<float>& text_modulation_values,
    const std::vector<float>& image_modulation_values,
    const BonsaiFluxRotaryEmbedding& text_rotary,
    const BonsaiFluxRotaryEmbedding& image_rotary,
    uint64_t batch,
    std::vector<float>* text,
    std::vector<float>* image
) {
    const uint64_t text_sequence_length = text_rotary.token_count;
    const uint64_t image_sequence_length = image_rotary.token_count;
    const uint64_t dimensions = block.dimensions;
    require_sequence_shape(*text, batch, text_sequence_length, dimensions, "double text");
    require_sequence_shape(*image, batch, image_sequence_length, dimensions, "double image");

    const BonsaiFluxDoubleModulation text_modulation = bonsai_flux_split_double_modulation(
        text_modulation_values,
        batch,
        dimensions
    );
    const BonsaiFluxDoubleModulation image_modulation = bonsai_flux_split_double_modulation(
        image_modulation_values,
        batch,
        dimensions
    );
    const std::vector<float> normalized_text_msa = bonsai_flux_apply_modulated_layer_norm(
        *text,
        text_modulation.shift_msa,
        text_modulation.scale_msa,
        batch,
        text_sequence_length,
        dimensions,
        1e-6F
    );
    const std::vector<float> normalized_image_msa = bonsai_flux_apply_modulated_layer_norm(
        *image,
        image_modulation.shift_msa,
        image_modulation.scale_msa,
        batch,
        image_sequence_length,
        dimensions,
        1e-6F
    );

    const uint64_t combined_sequence_length = checked_add(
        text_sequence_length,
        image_sequence_length,
        "double attention sequence"
    );
    const uint64_t batch_heads = checked_multiply(batch, block.heads, "double attention heads");
    const std::vector<float> full_queries = bonsai_flux_concat_head_sequences(
        flux_apply_projection_rope(
            bonsai_linear_sequence(block.add_q, normalized_text_msa, batch, text_sequence_length),
            block.norm_added_q,
            text_rotary.cos,
            text_rotary.sin,
            batch,
            text_sequence_length,
            block.heads,
            block.head_dimension
        ),
        flux_apply_projection_rope(
            bonsai_linear_sequence(block.to_q, normalized_image_msa, batch, image_sequence_length),
            block.norm_q,
            image_rotary.cos,
            image_rotary.sin,
            batch,
            image_sequence_length,
            block.heads,
            block.head_dimension
        ),
        batch,
        block.heads,
        text_sequence_length,
        image_sequence_length,
        block.head_dimension
    );
    const std::vector<float> full_keys = bonsai_flux_concat_head_sequences(
        flux_apply_projection_rope(
            bonsai_linear_sequence(block.add_k, normalized_text_msa, batch, text_sequence_length),
            block.norm_added_k,
            text_rotary.cos,
            text_rotary.sin,
            batch,
            text_sequence_length,
            block.heads,
            block.head_dimension
        ),
        flux_apply_projection_rope(
            bonsai_linear_sequence(block.to_k, normalized_image_msa, batch, image_sequence_length),
            block.norm_k,
            image_rotary.cos,
            image_rotary.sin,
            batch,
            image_sequence_length,
            block.heads,
            block.head_dimension
        ),
        batch,
        block.heads,
        text_sequence_length,
        image_sequence_length,
        block.head_dimension
    );
    const std::vector<float> full_values = bonsai_flux_concat_head_sequences(
        bonsai_flux_sequence_to_heads(
            bonsai_linear_sequence(block.add_v, normalized_text_msa, batch, text_sequence_length),
            batch,
            text_sequence_length,
            block.heads,
            block.head_dimension
        ),
        bonsai_flux_sequence_to_heads(
            bonsai_linear_sequence(block.to_v, normalized_image_msa, batch, image_sequence_length),
            batch,
            image_sequence_length,
            block.heads,
            block.head_dimension
        ),
        batch,
        block.heads,
        text_sequence_length,
        image_sequence_length,
        block.head_dimension
    );
    const BonsaiFluxHeadSequenceParts attention_parts = bonsai_flux_split_head_sequences(
        bonsai_scaled_dot_product_attention(
            full_queries,
            full_keys,
            full_values,
            {},
            batch_heads,
            combined_sequence_length,
            block.head_dimension,
            1.0F / std::sqrt(static_cast<float>(block.head_dimension))
        ),
        batch,
        block.heads,
        text_sequence_length,
        image_sequence_length,
        block.head_dimension
    );
    const std::vector<float> text_attention_update = bonsai_linear_sequence(
        block.to_add_out,
        bonsai_flux_heads_to_sequence(
            attention_parts.first,
            batch,
            text_sequence_length,
            block.heads,
            block.head_dimension
        ),
        batch,
        text_sequence_length
    );
    const std::vector<float> image_attention_update = bonsai_linear_sequence(
        block.to_out,
        bonsai_flux_heads_to_sequence(
            attention_parts.second,
            batch,
            image_sequence_length,
            block.heads,
            block.head_dimension
        ),
        batch,
        image_sequence_length
    );

    std::vector<float> text_after_attention = bonsai_flux_apply_gated_residual(
        *text,
        text_attention_update,
        text_modulation.gate_msa,
        batch,
        text_sequence_length,
        dimensions
    );
    std::vector<float> image_after_attention = bonsai_flux_apply_gated_residual(
        *image,
        image_attention_update,
        image_modulation.gate_msa,
        batch,
        image_sequence_length,
        dimensions
    );
    const std::vector<float> normalized_text_mlp = bonsai_flux_apply_modulated_layer_norm(
        text_after_attention,
        text_modulation.shift_mlp,
        text_modulation.scale_mlp,
        batch,
        text_sequence_length,
        dimensions,
        1e-6F
    );
    const std::vector<float> normalized_image_mlp = bonsai_flux_apply_modulated_layer_norm(
        image_after_attention,
        image_modulation.shift_mlp,
        image_modulation.scale_mlp,
        batch,
        image_sequence_length,
        dimensions,
        1e-6F
    );

    *text = bonsai_flux_apply_gated_residual(
        text_after_attention,
        flux_swiglu_projection(
            block.ff_context_in,
            block.ff_context_out,
            normalized_text_mlp,
            batch,
            text_sequence_length,
            block.mlp_hidden_dimensions
        ),
        text_modulation.gate_mlp,
        batch,
        text_sequence_length,
        dimensions
    );
    *image = bonsai_flux_apply_gated_residual(
        image_after_attention,
        flux_swiglu_projection(
            block.ff_in,
            block.ff_out,
            normalized_image_mlp,
            batch,
            image_sequence_length,
            block.mlp_hidden_dimensions
        ),
        image_modulation.gate_mlp,
        batch,
        image_sequence_length,
        dimensions
    );
}

std::vector<float> apply_flux_single_block(
    const BonsaiFluxSingleBlockViews& block,
    const std::vector<float>& modulation_values,
    const std::vector<float>& rotary_cos,
    const std::vector<float>& rotary_sin,
    const std::vector<float>& hidden,
    uint64_t batch,
    uint64_t sequence_length
) {
    const uint64_t dimensions = block.dimensions;
    require_sequence_shape(hidden, batch, sequence_length, dimensions, "single hidden");
    const BonsaiFluxSingleModulation modulation = bonsai_flux_split_single_modulation(
        modulation_values,
        batch,
        dimensions
    );
    const std::vector<float> normalized = bonsai_flux_apply_modulated_layer_norm(
        hidden,
        modulation.shift,
        modulation.scale,
        batch,
        sequence_length,
        dimensions,
        1e-6F
    );
    const BonsaiFluxSingleProjectionParts parts = bonsai_flux_split_single_projection(
        bonsai_linear_sequence(block.qkv_mlp_proj, normalized, batch, sequence_length),
        batch,
        sequence_length,
        dimensions,
        block.mlp_hidden_dimensions
    );
    const uint64_t batch_heads = checked_multiply(batch, block.heads, "single batch heads");
    const std::vector<float> attended = bonsai_flux_heads_to_sequence(
        bonsai_scaled_dot_product_attention(
            flux_apply_projection_rope(
                parts.query,
                block.norm_q,
                rotary_cos,
                rotary_sin,
                batch,
                sequence_length,
                block.heads,
                block.head_dimension
            ),
            flux_apply_projection_rope(
                parts.key,
                block.norm_k,
                rotary_cos,
                rotary_sin,
                batch,
                sequence_length,
                block.heads,
                block.head_dimension
            ),
            bonsai_flux_sequence_to_heads(
                parts.value,
                batch,
                sequence_length,
                block.heads,
                block.head_dimension
            ),
            {},
            batch_heads,
            sequence_length,
            block.head_dimension,
            1.0F / std::sqrt(static_cast<float>(block.head_dimension))
        ),
        batch,
        sequence_length,
        block.heads,
        block.head_dimension
    );
    const std::vector<float> projection_input = bonsai_flux_concat_last_dimension(
        attended,
        bonsai_swiglu_last_dimension(
            parts.mlp_values,
            checked_multiply(block.mlp_hidden_dimensions, 2U, "single swiglu width")
        ),
        batch,
        sequence_length,
        dimensions,
        block.mlp_hidden_dimensions
    );
    return bonsai_flux_apply_gated_residual(
        hidden,
        bonsai_linear_sequence(block.out_proj, projection_input, batch, sequence_length),
        modulation.gate,
        batch,
        sequence_length,
        dimensions
    );
}

BonsaiFluxDoubleBlockViews require_double_block_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    uint64_t block_index,
    int bits,
    int group_size
) {
    const std::string block = "transformer_blocks." + std::to_string(block_index);
    const std::string attn = block + ".attn";
    return BonsaiFluxDoubleBlockViews {
        require_packed_linear_view_checked(storage, index, attn + ".to_q.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".to_k.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".to_v.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".add_q_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".add_k_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".add_v_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".to_out.0.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, attn + ".to_add_out.weight", bits, group_size, FLUX_DIM, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, block + ".ff.linear_in.weight", bits, group_size, FLUX_DIM, FLUX_MLP_HIDDEN * 2),
        require_packed_linear_view_checked(storage, index, block + ".ff.linear_out.weight", bits, group_size, FLUX_MLP_HIDDEN, FLUX_DIM),
        require_packed_linear_view_checked(storage, index, block + ".ff_context.linear_in.weight", bits, group_size, FLUX_DIM, FLUX_MLP_HIDDEN * 2),
        require_packed_linear_view_checked(storage, index, block + ".ff_context.linear_out.weight", bits, group_size, FLUX_MLP_HIDDEN, FLUX_DIM),
        require_norm_view_checked(storage, index, attn + ".norm_q.weight", 128),
        require_norm_view_checked(storage, index, attn + ".norm_k.weight", 128),
        require_norm_view_checked(storage, index, attn + ".norm_added_q.weight", 128),
        require_norm_view_checked(storage, index, attn + ".norm_added_k.weight", 128),
        FLUX_DIM,
        24,
        128,
        FLUX_MLP_HIDDEN,
    };
}

BonsaiFluxSingleBlockViews require_single_block_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    uint64_t block_index,
    int bits,
    int group_size
) {
    const std::string attn = "single_transformer_blocks." +
        std::to_string(block_index) +
        ".attn";
    return BonsaiFluxSingleBlockViews {
        require_packed_linear_view_checked(
            storage,
            index,
            attn + ".to_qkv_mlp_proj.weight",
            bits,
            group_size,
            FLUX_DIM,
            FLUX_DIM * 3 + FLUX_MLP_HIDDEN * 2
        ),
        require_packed_linear_view_checked(
            storage,
            index,
            attn + ".to_out.weight",
            bits,
            group_size,
            FLUX_DIM + FLUX_MLP_HIDDEN,
            FLUX_DIM
        ),
        require_norm_view_checked(storage, index, attn + ".norm_q.weight", 128),
        require_norm_view_checked(storage, index, attn + ".norm_k.weight", 128),
        FLUX_DIM,
        24,
        128,
        FLUX_MLP_HIDDEN,
    };
}

void require_double_block(
    const BonsaiSafetensorsIndex& index,
    uint64_t block_index,
    int bits,
    int group_size,
    uint64_t* count
) {
    const std::string block = "transformer_blocks." + std::to_string(block_index);
    const std::string attn = block + ".attn";

    require_packed_linear(index, attn + ".to_q.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".to_k.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".to_v.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".add_q_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".add_k_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".add_v_proj.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".to_out.0.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(index, attn + ".to_add_out.weight", bits, group_size, FLUX_DIM, FLUX_DIM, count);
    require_packed_linear(
        index,
        block + ".ff.linear_in.weight",
        bits,
        group_size,
        FLUX_DIM,
        FLUX_MLP_HIDDEN * 2,
        count
    );
    require_packed_linear(
        index,
        block + ".ff.linear_out.weight",
        bits,
        group_size,
        FLUX_MLP_HIDDEN,
        FLUX_DIM,
        count
    );
    require_packed_linear(
        index,
        block + ".ff_context.linear_in.weight",
        bits,
        group_size,
        FLUX_DIM,
        FLUX_MLP_HIDDEN * 2,
        count
    );
    require_packed_linear(
        index,
        block + ".ff_context.linear_out.weight",
        bits,
        group_size,
        FLUX_MLP_HIDDEN,
        FLUX_DIM,
        count
    );

    require_norm(index, attn + ".norm_q.weight", 128, count);
    require_norm(index, attn + ".norm_k.weight", 128, count);
    require_norm(index, attn + ".norm_added_q.weight", 128, count);
    require_norm(index, attn + ".norm_added_k.weight", 128, count);
}

void require_single_block(
    const BonsaiSafetensorsIndex& index,
    uint64_t block_index,
    int bits,
    int group_size,
    uint64_t* count
) {
    const std::string attn = "single_transformer_blocks." +
        std::to_string(block_index) +
        ".attn";

    require_packed_linear(
        index,
        attn + ".to_qkv_mlp_proj.weight",
        bits,
        group_size,
        FLUX_DIM,
        FLUX_DIM * 3 + FLUX_MLP_HIDDEN * 2,
        count
    );
    require_packed_linear(
        index,
        attn + ".to_out.weight",
        bits,
        group_size,
        FLUX_DIM + FLUX_MLP_HIDDEN,
        FLUX_DIM,
        count
    );
    require_norm(index, attn + ".norm_q.weight", 128, count);
    require_norm(index, attn + ".norm_k.weight", 128, count);
}

} // namespace

BonsaiFluxTransformerInventorySummary bonsai_require_flux_transformer_tensors(
    const BonsaiSafetensorsIndex& index,
    int bits,
    int group_size
) {
    BonsaiFluxTransformerInventorySummary summary {
        FLUX_DOUBLE_BLOCKS,
        FLUX_SINGLE_BLOCKS,
        0,
    };

    require_dense_linear(
        index,
        "x_embedder.weight",
        FLUX_LATENT_CHANNELS,
        FLUX_DIM,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "context_embedder.weight",
        FLUX_TEXT_HIDDEN,
        FLUX_DIM,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "norm_out.linear.weight",
        FLUX_DIM,
        FLUX_DIM * 2,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "proj_out.weight",
        FLUX_DIM,
        FLUX_LATENT_CHANNELS,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "time_guidance_embed.timestep_embedder.linear_1.weight",
        FLUX_TIME_EMBED,
        FLUX_DIM,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "time_guidance_embed.timestep_embedder.linear_2.weight",
        FLUX_DIM,
        FLUX_DIM,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "double_stream_modulation_img.linear.weight",
        FLUX_DIM,
        FLUX_DIM * 6,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "double_stream_modulation_txt.linear.weight",
        FLUX_DIM,
        FLUX_DIM * 6,
        &summary.logical_tensor_count
    );
    require_dense_linear(
        index,
        "single_stream_modulation.linear.weight",
        FLUX_DIM,
        FLUX_DIM * 3,
        &summary.logical_tensor_count
    );

    for (uint64_t index_value = 0; index_value < FLUX_DOUBLE_BLOCKS; index_value++) {
        require_double_block(
            index,
            index_value,
            bits,
            group_size,
            &summary.logical_tensor_count
        );
    }
    for (uint64_t index_value = 0; index_value < FLUX_SINGLE_BLOCKS; index_value++) {
        require_single_block(
            index,
            index_value,
            bits,
            group_size,
            &summary.logical_tensor_count
        );
    }

    return summary;
}

BonsaiFluxTransformerViews bonsai_require_flux_transformer_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    int bits,
    int group_size
) {
    BonsaiFluxTransformerViews views {
        require_dense_linear_view_checked(storage, index, "x_embedder.weight", FLUX_LATENT_CHANNELS, FLUX_DIM),
        require_dense_linear_view_checked(storage, index, "context_embedder.weight", FLUX_TEXT_HIDDEN, FLUX_DIM),
        require_dense_linear_view_checked(storage, index, "time_guidance_embed.timestep_embedder.linear_1.weight", FLUX_TIME_EMBED, FLUX_DIM),
        require_dense_linear_view_checked(storage, index, "time_guidance_embed.timestep_embedder.linear_2.weight", FLUX_DIM, FLUX_DIM),
        require_dense_linear_view_checked(storage, index, "double_stream_modulation_img.linear.weight", FLUX_DIM, FLUX_DIM * 6),
        require_dense_linear_view_checked(storage, index, "double_stream_modulation_txt.linear.weight", FLUX_DIM, FLUX_DIM * 6),
        require_dense_linear_view_checked(storage, index, "single_stream_modulation.linear.weight", FLUX_DIM, FLUX_DIM * 3),
        require_dense_linear_view_checked(storage, index, "norm_out.linear.weight", FLUX_DIM, FLUX_DIM * 2),
        require_dense_linear_view_checked(storage, index, "proj_out.weight", FLUX_DIM, FLUX_LATENT_CHANNELS),
        {},
        {},
        FLUX_DIM,
        FLUX_TEXT_HIDDEN,
        FLUX_LATENT_CHANNELS,
        FLUX_TIME_EMBED,
    };
    views.double_blocks.reserve(static_cast<size_t>(FLUX_DOUBLE_BLOCKS));
    for (uint64_t block = 0; block < FLUX_DOUBLE_BLOCKS; block++) {
        views.double_blocks.push_back(require_double_block_views(
            storage,
            index,
            block,
            bits,
            group_size
        ));
    }
    views.single_blocks.reserve(static_cast<size_t>(FLUX_SINGLE_BLOCKS));
    for (uint64_t block = 0; block < FLUX_SINGLE_BLOCKS; block++) {
        views.single_blocks.push_back(require_single_block_views(
            storage,
            index,
            block,
            bits,
            group_size
        ));
    }
    return views;
}

BonsaiFluxTransformerOutput bonsai_flux_transformer_forward(
    const BonsaiFluxTransformerViews& views,
    const std::vector<float>& latent_tokens,
    const std::vector<float>& prompt_embeddings,
    const std::vector<std::array<float, 4>>& image_ids,
    const std::vector<std::array<float, 4>>& text_ids,
    float timestep
) {
    if (image_ids.empty() || text_ids.empty()) {
        throw std::runtime_error("Bonsai Flux transformer ids must not be empty.");
    }
    if (!std::isfinite(timestep)) {
        throw std::runtime_error("Bonsai Flux transformer timestep must be finite.");
    }
    if (views.dimensions == 0 ||
        views.text_hidden_size == 0 ||
        views.latent_channels == 0 ||
        views.timestep_embedding_size == 0 ||
        views.double_blocks.empty() ||
        views.single_blocks.empty()) {
        throw std::runtime_error("Bonsai Flux transformer views are incomplete.");
    }

    const uint64_t image_sequence_length = static_cast<uint64_t>(image_ids.size());
    const uint64_t text_sequence_length = static_cast<uint64_t>(text_ids.size());
    const uint64_t latent_batch_stride = checked_multiply(
        image_sequence_length,
        views.latent_channels,
        "flux latent stride"
    );
    if (latent_batch_stride == 0 ||
        latent_tokens.empty() ||
        latent_tokens.size() % checked_size(latent_batch_stride, "flux latent stride") != 0) {
        throw std::runtime_error("Bonsai Flux latent token shape mismatch.");
    }
    const uint64_t batch = static_cast<uint64_t>(
        latent_tokens.size() / checked_size(latent_batch_stride, "flux latent stride")
    );
    require_sequence_shape(
        prompt_embeddings,
        batch,
        text_sequence_length,
        views.text_hidden_size,
        "flux prompt embeddings"
    );

    const float timestep_value = timestep <= 1.0F ? timestep * 1000.0F : timestep;
    const BonsaiFluxTimestepEmbedding timestep_embedding = bonsai_flux_timestep_embedding(
        std::vector<float>(static_cast<size_t>(batch), timestep_value),
        views.timestep_embedding_size
    );
    std::vector<float> timestep_values = linear_batch_vector(
        views.timestep_linear1,
        timestep_embedding.values,
        batch
    );
    timestep_values = linear_batch_vector(
        views.timestep_linear2,
        bonsai_silu(timestep_values),
        batch
    );
    const std::vector<float> modulation_input = bonsai_silu(timestep_values);

    const BonsaiFluxRotaryEmbedding text_rotary = bonsai_flux_pos_embed(text_ids);
    const BonsaiFluxRotaryEmbedding image_rotary = bonsai_flux_pos_embed(image_ids);
    const std::vector<float> combined_rotary_cos = concat_rotary_rows(
        text_rotary,
        image_rotary,
        true
    );
    const std::vector<float> combined_rotary_sin = concat_rotary_rows(
        text_rotary,
        image_rotary,
        false
    );

    std::vector<float> image = bonsai_linear_sequence(
        views.x_embedder,
        latent_tokens,
        batch,
        image_sequence_length
    );
    std::vector<float> text = bonsai_linear_sequence(
        views.context_embedder,
        prompt_embeddings,
        batch,
        text_sequence_length
    );

    const std::vector<float> image_double_modulation = linear_batch_vector(
        views.double_modulation_img,
        modulation_input,
        batch
    );
    const std::vector<float> text_double_modulation = linear_batch_vector(
        views.double_modulation_txt,
        modulation_input,
        batch
    );
    for (size_t index = 0; index < views.double_blocks.size(); index++) {
        const BonsaiFluxDoubleBlockViews& block = views.double_blocks[index];
        log_flux_block_phase(
            "flux_double_block_start",
            static_cast<uint64_t>(index + 1U),
            text_sequence_length,
            image_sequence_length
        );
        apply_flux_double_block(
            block,
            text_double_modulation,
            image_double_modulation,
            text_rotary,
            image_rotary,
            batch,
            &text,
            &image
        );
        log_flux_block_phase(
            "flux_double_block_done",
            static_cast<uint64_t>(index + 1U),
            text_sequence_length,
            image_sequence_length
        );
    }

    const uint64_t combined_sequence_length = checked_add(
        text_sequence_length,
        image_sequence_length,
        "flux combined sequence"
    );
    std::vector<float> hidden = concat_token_sequences(
        text,
        image,
        batch,
        text_sequence_length,
        image_sequence_length,
        views.dimensions
    );
    const std::vector<float> single_modulation = linear_batch_vector(
        views.single_modulation,
        modulation_input,
        batch
    );
    for (size_t index = 0; index < views.single_blocks.size(); index++) {
        const BonsaiFluxSingleBlockViews& block = views.single_blocks[index];
        log_flux_block_phase(
            "flux_single_block_start",
            static_cast<uint64_t>(index + 1U),
            combined_sequence_length,
            image_sequence_length
        );
        hidden = apply_flux_single_block(
            block,
            single_modulation,
            combined_rotary_cos,
            combined_rotary_sin,
            hidden,
            batch,
            combined_sequence_length
        );
        log_flux_block_phase(
            "flux_single_block_done",
            static_cast<uint64_t>(index + 1U),
            combined_sequence_length,
            image_sequence_length
        );
    }

    const std::vector<float> image_output = slice_token_sequence(
        hidden,
        batch,
        combined_sequence_length,
        views.dimensions,
        text_sequence_length,
        image_sequence_length
    );
    BonsaiFluxTransformerOutput output;
    output.batch = batch;
    output.sequence_length = image_sequence_length;
    output.channels = views.latent_channels;
    output.values = bonsai_linear_sequence(
        views.proj_out,
        bonsai_flux_final_projection_input(
            image_output,
            linear_batch_vector(views.norm_out_linear, modulation_input, batch),
            batch,
            image_sequence_length,
            views.dimensions,
            1e-6F
        ),
        batch,
        image_sequence_length
    );
    return output;
}

uint64_t bonsai_flux_single_block_byte_count(const BonsaiFluxSingleBlockViews& views) {
    uint64_t bytes = bonsai_linear_byte_count(views.qkv_mlp_proj);
    add_bytes(&bytes, bonsai_linear_byte_count(views.out_proj), "single out");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_q), "single norm q");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_k), "single norm k");
    return bytes;
}

uint64_t bonsai_flux_double_block_byte_count(const BonsaiFluxDoubleBlockViews& views) {
    uint64_t bytes = bonsai_linear_byte_count(views.to_q);
    add_bytes(&bytes, bonsai_linear_byte_count(views.to_k), "double to_k");
    add_bytes(&bytes, bonsai_linear_byte_count(views.to_v), "double to_v");
    add_bytes(&bytes, bonsai_linear_byte_count(views.add_q), "double add_q");
    add_bytes(&bytes, bonsai_linear_byte_count(views.add_k), "double add_k");
    add_bytes(&bytes, bonsai_linear_byte_count(views.add_v), "double add_v");
    add_bytes(&bytes, bonsai_linear_byte_count(views.to_out), "double to_out");
    add_bytes(&bytes, bonsai_linear_byte_count(views.to_add_out), "double to_add_out");
    add_bytes(&bytes, bonsai_linear_byte_count(views.ff_in), "double ff_in");
    add_bytes(&bytes, bonsai_linear_byte_count(views.ff_out), "double ff_out");
    add_bytes(&bytes, bonsai_linear_byte_count(views.ff_context_in), "double ff_context_in");
    add_bytes(&bytes, bonsai_linear_byte_count(views.ff_context_out), "double ff_context_out");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_q), "double norm_q");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_k), "double norm_k");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_added_q), "double norm_added_q");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.norm_added_k), "double norm_added_k");
    return bytes;
}

uint64_t bonsai_flux_transformer_byte_count(const BonsaiFluxTransformerViews& views) {
    uint64_t bytes = bonsai_linear_byte_count(views.x_embedder);
    add_bytes(&bytes, bonsai_linear_byte_count(views.context_embedder), "context embedder");
    add_bytes(&bytes, bonsai_linear_byte_count(views.timestep_linear1), "timestep linear1");
    add_bytes(&bytes, bonsai_linear_byte_count(views.timestep_linear2), "timestep linear2");
    add_bytes(&bytes, bonsai_linear_byte_count(views.double_modulation_img), "double mod img");
    add_bytes(&bytes, bonsai_linear_byte_count(views.double_modulation_txt), "double mod txt");
    add_bytes(&bytes, bonsai_linear_byte_count(views.single_modulation), "single mod");
    add_bytes(&bytes, bonsai_linear_byte_count(views.norm_out_linear), "norm out");
    add_bytes(&bytes, bonsai_linear_byte_count(views.proj_out), "proj out");
    for (const BonsaiFluxDoubleBlockViews& block : views.double_blocks) {
        add_bytes(&bytes, bonsai_flux_double_block_byte_count(block), "double block");
    }
    for (const BonsaiFluxSingleBlockViews& block : views.single_blocks) {
        add_bytes(&bytes, bonsai_flux_single_block_byte_count(block), "single block");
    }
    return bytes;
}
