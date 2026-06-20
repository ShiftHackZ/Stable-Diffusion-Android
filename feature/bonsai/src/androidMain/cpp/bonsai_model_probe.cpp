#include "bonsai_activation.h"
#include "bonsai_model_probe.h"

#include "bonsai_attention.h"
#include "bonsai_dequant.h"
#include "bonsai_embedding.h"
#include "bonsai_flux_attention_layout.h"
#include "bonsai_flux_double_block.h"
#include "bonsai_flux_modulation.h"
#include "bonsai_flux_output.h"
#include "bonsai_flux_pos_embed.h"
#include "bonsai_flux_rope.h"
#include "bonsai_flux_single_block.h"
#include "bonsai_flux_time_embedding.h"
#include "bonsai_flux_transformer.h"
#include "bonsai_flux_vae.h"
#include "bonsai_latents.h"
#include "bonsai_layer_norm.h"
#include "bonsai_linear.h"
#include "bonsai_matmul.h"
#include "bonsai_model_config.h"
#include "bonsai_norm.h"
#include "bonsai_packed_weight.h"
#include "bonsai_prompt.h"
#include "bonsai_qwen.h"
#include "bonsai_rotary.h"
#include "bonsai_scheduler.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"
#include "bonsai_tokenizer.h"
#include "bonsai_vae_decoder.h"
#include "bonsai_vae_ops.h"

#include <algorithm>
#include <array>
#include <cmath>
#include <cstdlib>
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>

namespace {

void require_text_encoder_tensors(const BonsaiSafetensorsIndex& tensors) {
    const std::string embedding_key = tensors.resolve_model_prefixed_key("embed_tokens.weight");
    tensors.require_packed_weight(embedding_key, 4, 64);
    bonsai_require_qwen_text_encoder_tensors(tensors);
}

BonsaiFluxTransformerInventorySummary require_transformer_tensors(
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiQuantizationConfig& quantization
) {
    return bonsai_require_flux_transformer_tensors(
        tensors,
        quantization.bits,
        quantization.group_size
    );
}

BonsaiFluxVaeInventorySummary require_vae_tensors(
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiFluxVaeConfig& config
) {
    return bonsai_require_flux_vae_tensors(tensors, config);
}

uint64_t require_tensor_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const std::string& key
) {
    return storage.require_view(tensors, key).byte_count;
}

std::vector<float> synthetic_input(uint64_t size) {
    std::vector<float> input;
    input.reserve(static_cast<size_t>(size));
    for (uint64_t index = 0; index < size; index++) {
        input.push_back((static_cast<float>(index % 7U) - 3.0F) * 0.125F);
    }
    return input;
}

uint64_t require_packed_weight_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiPackedWeightDescriptor& weight,
    double* dequant_checksum,
    double* matvec_checksum,
    double* linear_checksum
) {
    const BonsaiPackedWeightViews views = bonsai_require_packed_weight_views(
        storage,
        tensors,
        weight
    );
    if (views.packed) {
        const std::vector<float> row = bonsai_dequantize_packed_row(views, 0);
        const size_t limit = std::min<size_t>(row.size(), 32);
        for (size_t index = 0; index < limit; index++) {
            *dequant_checksum += static_cast<double>(row[index]);
        }
        *matvec_checksum += static_cast<double>(
            bonsai_quantized_matvec_row(views, synthetic_input(views.input_values), 0)
        );
    }
    const BonsaiLinearViews linear = bonsai_require_packed_linear_views(
        storage,
        tensors,
        weight,
        weight.weight_key.substr(0, weight.weight_key.size() - std::string(".weight").size()) +
            ".bias"
    );
    *linear_checksum += static_cast<double>(
        bonsai_linear_row(linear, synthetic_input(linear.input_values), 0)
    );
    return bonsai_linear_byte_count(linear);
}

uint64_t require_dense_weight_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const std::string& key,
    double* dense_checksum,
    double* linear_checksum
) {
    const BonsaiDenseWeightViews views = bonsai_require_dense_weight_view(
        storage,
        tensors,
        key
    );
    *dense_checksum += static_cast<double>(
        bonsai_dense_matvec_row(views, synthetic_input(views.input_values), 0)
    );

    const BonsaiLinearViews linear = bonsai_require_dense_linear_views(
        storage,
        tensors,
        key,
        key.substr(0, key.size() - std::string(".weight").size()) + ".bias"
    );
    *linear_checksum += static_cast<double>(
        bonsai_linear_row(linear, synthetic_input(linear.input_values), 0)
    );
    return bonsai_linear_byte_count(linear);
}

uint64_t require_embedding_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiPackedWeightDescriptor& descriptor,
    double* embedding_checksum
) {
    const BonsaiEmbeddingViews views = bonsai_require_embedding_views(storage, tensors, descriptor);
    const std::vector<float> lookup = bonsai_embedding_lookup(views, {0, 1, 2});
    const size_t limit = std::min<size_t>(lookup.size(), 96);
    for (size_t index = 0; index < limit; index++) {
        *embedding_checksum += static_cast<double>(lookup[index]);
    }
    return bonsai_embedding_byte_count(views);
}

uint64_t require_rms_norm_view(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const std::string& key,
    double* norm_checksum
) {
    const BonsaiRmsNormWeightViews views = bonsai_require_rms_norm_weight(storage, tensors, key);
    const std::vector<float> output = bonsai_rms_norm(synthetic_input(views.dimensions), views, 1e-6F);
    const size_t limit = std::min<size_t>(output.size(), 64);
    for (size_t index = 0; index < limit; index++) {
        *norm_checksum += static_cast<double>(output[index]);
    }
    return bonsai_rms_norm_byte_count(views);
}

double activation_checksum() {
    const std::vector<float> gate = synthetic_input(128);
    const std::vector<float> up = synthetic_input(128);
    const std::vector<float> output = bonsai_silu_times(gate, up);

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_activation_checksum() {
    const std::vector<float> output = bonsai_swiglu_last_dimension(
        synthetic_input(3 * 16),
        16
    );

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_layer_norm_checksum() {
    std::vector<float> weight;
    std::vector<float> bias;
    weight.reserve(16);
    bias.reserve(16);
    for (uint64_t index = 0; index < 16; index++) {
        weight.push_back(1.0F + static_cast<float>(index) * 0.01F);
        bias.push_back((static_cast<float>(index % 5U) - 2.0F) * 0.05F);
    }

    const std::vector<float> output = bonsai_layer_norm(
        synthetic_input(3 * 16),
        16,
        1e-6F,
        &weight,
        &bias
    );

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double rotary_checksum() {
    const std::vector<float> input = synthetic_input(256);
    const std::vector<float> output = bonsai_apply_rotary_to_heads(
        input,
        128,
        3,
        1000000.0F
    );

    double checksum = 0.0;
    const size_t limit = std::min<size_t>(output.size(), 128);
    for (size_t index = 0; index < limit; index++) {
        checksum += static_cast<double>(output[index]);
    }
    return checksum;
}

double flux_time_embedding_checksum() {
    const BonsaiFluxTimestepEmbedding embedding = bonsai_flux_timestep_embedding(
        {1000.0F, 500.0F, 125.0F},
        256
    );
    if (embedding.timestep_count != 3 || embedding.dimensions != 256) {
        throw std::runtime_error("Bonsai Flux timestep embedding shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : embedding.values) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_pos_embed_checksum() {
    const BonsaiFluxRotaryEmbedding embedding = bonsai_flux_pos_embed({
        std::array<float, 4> {0.0F, 0.0F, 0.0F, 0.0F},
        std::array<float, 4> {0.0F, 0.0F, 1.0F, 2.0F},
        std::array<float, 4> {0.0F, 1.0F, 2.0F, 3.0F},
    });
    if (embedding.token_count != 3 || embedding.dimensions != 64) {
        throw std::runtime_error("Bonsai Flux position embedding shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : embedding.cos) {
        checksum += static_cast<double>(value);
    }
    for (float value : embedding.sin) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_rope_checksum() {
    const uint64_t heads = 2;
    const uint64_t sequence_length = 3;
    const uint64_t head_dimension = 8;
    std::vector<float> norm_weight;
    norm_weight.reserve(static_cast<size_t>(head_dimension));
    for (uint64_t index = 0; index < head_dimension; index++) {
        norm_weight.push_back(1.0F + 0.01F * static_cast<float>(index));
    }

    const BonsaiFluxRotaryEmbedding embedding = bonsai_flux_pos_embed({
        std::array<float, 4> {0.0F, 0.0F, 0.0F, 0.0F},
        std::array<float, 4> {0.0F, 1.0F, 0.0F, 1.0F},
        std::array<float, 4> {0.0F, 2.0F, 1.0F, 2.0F},
    });
    std::vector<float> cos_values;
    std::vector<float> sin_values;
    cos_values.reserve(static_cast<size_t>(sequence_length * head_dimension / 2));
    sin_values.reserve(static_cast<size_t>(sequence_length * head_dimension / 2));
    for (uint64_t position = 0; position < sequence_length; position++) {
        for (uint64_t index = 0; index < head_dimension / 2; index++) {
            cos_values.push_back(
                embedding.cos[static_cast<size_t>(position * embedding.dimensions + index)]
            );
            sin_values.push_back(
                embedding.sin[static_cast<size_t>(position * embedding.dimensions + index)]
            );
        }
    }

    const std::vector<float> output = bonsai_flux_apply_rms_norm_and_rope(
        synthetic_input(heads * sequence_length * head_dimension),
        norm_weight,
        cos_values,
        sin_values,
        heads,
        sequence_length,
        head_dimension,
        1e-5F
    );

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_modulation_checksum() {
    const uint64_t batch = 1;
    const uint64_t sequence_length = 3;
    const uint64_t dimensions = 16;
    const BonsaiFluxSingleModulation single = bonsai_flux_split_single_modulation(
        synthetic_input(batch * 3 * dimensions),
        batch,
        dimensions
    );
    const BonsaiFluxDoubleModulation double_mod = bonsai_flux_split_double_modulation(
        synthetic_input(batch * 6 * dimensions),
        batch,
        dimensions
    );
    const BonsaiFluxNormOutModulation norm_out = bonsai_flux_split_norm_out_modulation(
        synthetic_input(batch * 2 * dimensions),
        batch,
        dimensions
    );
    const std::vector<float> normed = bonsai_flux_apply_modulated_layer_norm(
        synthetic_input(batch * sequence_length * dimensions),
        single.shift,
        single.scale,
        batch,
        sequence_length,
        dimensions,
        1e-6F
    );
    const std::vector<float> gated = bonsai_flux_apply_gated_residual(
        synthetic_input(batch * sequence_length * dimensions),
        normed,
        single.gate,
        batch,
        sequence_length,
        dimensions
    );
    const std::vector<float> final_norm = bonsai_flux_apply_modulated_layer_norm(
        gated,
        norm_out.shift,
        norm_out.scale,
        batch,
        sequence_length,
        dimensions,
        1e-6F
    );

    double checksum = 0.0;
    for (float value : double_mod.shift_msa) {
        checksum += static_cast<double>(value);
    }
    for (float value : double_mod.scale_mlp) {
        checksum += static_cast<double>(value);
    }
    for (float value : double_mod.gate_mlp) {
        checksum += static_cast<double>(value);
    }
    for (float value : final_norm) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_attention_layout_checksum() {
    const uint64_t batch = 1;
    const uint64_t sequence_length = 3;
    const uint64_t heads = 2;
    const uint64_t head_dimension = 8;
    const uint64_t dimensions = heads * head_dimension;
    const uint64_t mlp_hidden_dimensions = 24;
    const BonsaiFluxSingleProjectionParts parts = bonsai_flux_split_single_projection(
        synthetic_input(batch * sequence_length * (dimensions * 3 + mlp_hidden_dimensions * 2)),
        batch,
        sequence_length,
        dimensions,
        mlp_hidden_dimensions
    );
    const std::vector<float> query_heads = bonsai_flux_sequence_to_heads(
        parts.query,
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> key_heads = bonsai_flux_sequence_to_heads(
        parts.key,
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> value_heads = bonsai_flux_sequence_to_heads(
        parts.value,
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> attended_heads = bonsai_scaled_dot_product_attention(
        query_heads,
        key_heads,
        value_heads,
        {},
        batch * heads,
        sequence_length,
        head_dimension,
        1.0F / std::sqrt(static_cast<float>(head_dimension))
    );
    const std::vector<float> attended_sequence = bonsai_flux_heads_to_sequence(
        attended_heads,
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> mlp_output = bonsai_swiglu_last_dimension(
        parts.mlp_values,
        mlp_hidden_dimensions * 2
    );
    const std::vector<float> concatenated = bonsai_flux_concat_head_sequences(
        query_heads,
        value_heads,
        batch,
        heads,
        sequence_length,
        sequence_length,
        head_dimension
    );

    double checksum = 0.0;
    for (float value : attended_sequence) {
        checksum += static_cast<double>(value);
    }
    for (float value : mlp_output) {
        checksum += static_cast<double>(value);
    }
    for (float value : concatenated) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_single_block_checksum() {
    const uint64_t batch = 1;
    const uint64_t sequence_length = 3;
    const uint64_t heads = 2;
    const uint64_t head_dimension = 8;
    const uint64_t dimensions = heads * head_dimension;
    const uint64_t mlp_hidden_dimensions = 24;
    std::vector<float> norm_q_weight;
    std::vector<float> norm_k_weight;
    norm_q_weight.reserve(static_cast<size_t>(head_dimension));
    norm_k_weight.reserve(static_cast<size_t>(head_dimension));
    for (uint64_t index = 0; index < head_dimension; index++) {
        norm_q_weight.push_back(1.0F + 0.01F * static_cast<float>(index));
        norm_k_weight.push_back(0.9F + 0.02F * static_cast<float>(index));
    }

    const BonsaiFluxRotaryEmbedding embedding = bonsai_flux_pos_embed({
        std::array<float, 4> {0.0F, 0.0F, 0.0F, 0.0F},
        std::array<float, 4> {0.0F, 1.0F, 0.0F, 1.0F},
        std::array<float, 4> {0.0F, 2.0F, 1.0F, 2.0F},
    });
    std::vector<float> cos_values;
    std::vector<float> sin_values;
    cos_values.reserve(static_cast<size_t>(sequence_length * head_dimension / 2));
    sin_values.reserve(static_cast<size_t>(sequence_length * head_dimension / 2));
    for (uint64_t position = 0; position < sequence_length; position++) {
        for (uint64_t index = 0; index < head_dimension / 2; index++) {
            cos_values.push_back(
                embedding.cos[static_cast<size_t>(position * embedding.dimensions + index)]
            );
            sin_values.push_back(
                embedding.sin[static_cast<size_t>(position * embedding.dimensions + index)]
            );
        }
    }

    const BonsaiFluxSingleBlockReferenceOutput output = bonsai_flux_single_block_reference(
        synthetic_input(batch * sequence_length * dimensions),
        synthetic_input(batch * 3 * dimensions),
        synthetic_input(
            batch *
            sequence_length *
            (dimensions * 3 + mlp_hidden_dimensions * 2)
        ),
        synthetic_input(batch * sequence_length * dimensions),
        norm_q_weight,
        norm_k_weight,
        cos_values,
        sin_values,
        batch,
        sequence_length,
        heads,
        head_dimension,
        mlp_hidden_dimensions,
        1e-6F,
        1e-5F
    );
    if (output.out_projection_input_dimensions != dimensions + mlp_hidden_dimensions) {
        throw std::runtime_error("Bonsai Flux single block projection shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : output.normalized_hidden) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.out_projection_input) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.residual_output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_double_block_checksum() {
    const uint64_t batch = 1;
    const uint64_t text_sequence_length = 2;
    const uint64_t image_sequence_length = 3;
    const uint64_t heads = 2;
    const uint64_t head_dimension = 8;
    const uint64_t dimensions = heads * head_dimension;
    std::vector<float> text_norm_q;
    std::vector<float> text_norm_k;
    std::vector<float> image_norm_q;
    std::vector<float> image_norm_k;
    text_norm_q.reserve(static_cast<size_t>(head_dimension));
    text_norm_k.reserve(static_cast<size_t>(head_dimension));
    image_norm_q.reserve(static_cast<size_t>(head_dimension));
    image_norm_k.reserve(static_cast<size_t>(head_dimension));
    for (uint64_t index = 0; index < head_dimension; index++) {
        text_norm_q.push_back(1.0F + 0.01F * static_cast<float>(index));
        text_norm_k.push_back(0.95F + 0.015F * static_cast<float>(index));
        image_norm_q.push_back(0.9F + 0.02F * static_cast<float>(index));
        image_norm_k.push_back(1.05F + 0.005F * static_cast<float>(index));
    }

    const BonsaiFluxRotaryEmbedding embedding = bonsai_flux_pos_embed({
        std::array<float, 4> {0.0F, 0.0F, 0.0F, 0.0F},
        std::array<float, 4> {0.0F, 1.0F, 0.0F, 1.0F},
        std::array<float, 4> {0.0F, 2.0F, 1.0F, 2.0F},
        std::array<float, 4> {0.0F, 3.0F, 1.0F, 3.0F},
        std::array<float, 4> {0.0F, 4.0F, 2.0F, 4.0F},
    });
    std::vector<float> text_cos;
    std::vector<float> text_sin;
    std::vector<float> image_cos;
    std::vector<float> image_sin;
    text_cos.reserve(static_cast<size_t>(text_sequence_length * head_dimension / 2));
    text_sin.reserve(static_cast<size_t>(text_sequence_length * head_dimension / 2));
    image_cos.reserve(static_cast<size_t>(image_sequence_length * head_dimension / 2));
    image_sin.reserve(static_cast<size_t>(image_sequence_length * head_dimension / 2));
    for (uint64_t position = 0; position < text_sequence_length + image_sequence_length; position++) {
        std::vector<float>* target_cos = position < text_sequence_length ? &text_cos : &image_cos;
        std::vector<float>* target_sin = position < text_sequence_length ? &text_sin : &image_sin;
        for (uint64_t index = 0; index < head_dimension / 2; index++) {
            target_cos->push_back(
                embedding.cos[static_cast<size_t>(position * embedding.dimensions + index)]
            );
            target_sin->push_back(
                embedding.sin[static_cast<size_t>(position * embedding.dimensions + index)]
            );
        }
    }

    const BonsaiFluxDoubleBlockReferenceOutput output = bonsai_flux_double_block_reference(
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * 6 * dimensions),
        synthetic_input(batch * 6 * dimensions),
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * text_sequence_length * dimensions),
        synthetic_input(batch * image_sequence_length * dimensions),
        text_norm_q,
        text_norm_k,
        image_norm_q,
        image_norm_k,
        text_cos,
        text_sin,
        image_cos,
        image_sin,
        batch,
        text_sequence_length,
        image_sequence_length,
        heads,
        head_dimension,
        1e-6F,
        1e-5F
    );
    if (output.dimensions != dimensions ||
        output.text_sequence_length != text_sequence_length ||
        output.image_sequence_length != image_sequence_length) {
        throw std::runtime_error("Bonsai Flux double block output shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : output.normalized_text_msa) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.normalized_image_msa) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.attention_text) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.attention_image) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.normalized_text_mlp) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.normalized_image_mlp) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.text_output) {
        checksum += static_cast<double>(value);
    }
    for (float value : output.image_output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double flux_output_checksum() {
    const uint64_t batch = 1;
    const uint64_t image_sequence_length = 4;
    const uint64_t dimensions = 16;
    const std::vector<float> output = bonsai_flux_final_projection_input(
        synthetic_input(batch * image_sequence_length * dimensions),
        synthetic_input(batch * 2 * dimensions),
        batch,
        image_sequence_length,
        dimensions,
        1e-6F
    );

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double attention_checksum() {
    const uint64_t heads = 2;
    const uint64_t key_value_heads = 1;
    const uint64_t length = 4;
    const uint64_t head_dimension = 8;

    const std::vector<float> queries = synthetic_input(heads * length * head_dimension);
    const std::vector<float> keys = bonsai_repeat_kv_heads(
        synthetic_input(key_value_heads * length * head_dimension),
        key_value_heads,
        heads / key_value_heads,
        length,
        head_dimension
    );
    const std::vector<float> values = bonsai_repeat_kv_heads(
        synthetic_input(key_value_heads * length * head_dimension),
        key_value_heads,
        heads / key_value_heads,
        length,
        head_dimension
    );

    std::vector<float> mask;
    mask.reserve(static_cast<size_t>(length * length));
    for (uint64_t row = 0; row < length; row++) {
        for (uint64_t column = 0; column < length; column++) {
            mask.push_back(column > row ? -std::numeric_limits<float>::infinity() : 0.0F);
        }
    }

    const std::vector<float> output = bonsai_scaled_dot_product_attention(
        queries,
        keys,
        values,
        mask,
        heads,
        length,
        head_dimension,
        1.0F / std::sqrt(static_cast<float>(head_dimension))
    );

    double checksum = 0.0;
    for (float value : output) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double scheduler_checksum() {
    const BonsaiFlowMatchEulerSchedule schedule = bonsai_flow_match_euler_schedule(4096, 4);
    const std::vector<float> updated = bonsai_flow_match_euler_step(
        synthetic_input(64),
        1,
        synthetic_input(64),
        schedule
    );

    double checksum = 0.0;
    for (float value : schedule.timesteps) {
        checksum += static_cast<double>(value);
    }
    for (float value : schedule.sigmas) {
        checksum += static_cast<double>(value);
    }
    for (float value : updated) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double latent_checksum() {
    const BonsaiLatentShape shape = bonsai_packed_latent_shape(
        512,
        512,
        1,
        128,
        8
    );
    if (shape.latent_height != 32 || shape.latent_width != 32 || shape.sequence_length != 1024) {
        throw std::runtime_error("Bonsai latent shape mismatch.");
    }

    const uint64_t small_batch = 1;
    const uint64_t small_channels = 4;
    const uint64_t small_height = 2;
    const uint64_t small_width = 3;
    const std::vector<float> raw = synthetic_input(
        small_batch * small_channels * small_height * small_width
    );
    const std::vector<float> packed = bonsai_pack_latents_nchw(
        raw,
        small_batch,
        small_channels,
        small_height,
        small_width
    );
    const std::vector<float> unpacked = bonsai_unpack_packed_latents(
        packed,
        small_batch,
        small_height * small_width,
        small_channels,
        32,
        48,
        8
    );
    if (unpacked != raw) {
        throw std::runtime_error("Bonsai latent pack/unpack mismatch.");
    }

    const std::vector<int32_t> ids = bonsai_latent_grid_ids(1, 2, 3);
    double checksum = static_cast<double>(shape.sequence_length);
    for (float value : packed) {
        checksum += static_cast<double>(value);
    }
    for (int32_t value : ids) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double prompt_checksum() {
    const BonsaiQwenPromptSpec spec = bonsai_qwen_prompt_spec();
    const std::string formatted = bonsai_qwen_chat_formatted_prompt("Cat");
    if (formatted !=
        "<|im_start|>user\nCat<|im_end|>\n<|im_start|>assistant\n<think>\n\n</think>\n\n") {
        throw std::runtime_error("Bonsai Qwen prompt template mismatch.");
    }
    const std::vector<int32_t> text_ids = bonsai_qwen_text_ids(4);

    double checksum = static_cast<double>(spec.max_sequence_length) +
        static_cast<double>(spec.pad_token_id) +
        static_cast<double>(spec.eos_token_id);
    for (char value : formatted) {
        checksum += static_cast<double>(static_cast<unsigned char>(value));
    }
    for (int32_t value : text_ids) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double vae_ops_checksum() {
    BonsaiNchwTensor input {
        1,
        4,
        3,
        3,
        synthetic_input(1 * 4 * 3 * 3),
    };
    std::vector<float> norm_weight;
    std::vector<float> norm_bias;
    norm_weight.reserve(4);
    norm_bias.reserve(4);
    for (uint64_t index = 0; index < 4; index++) {
        norm_weight.push_back(1.0F + 0.02F * static_cast<float>(index));
        norm_bias.push_back((static_cast<float>(index) - 1.5F) * 0.03F);
    }

    const BonsaiNchwTensor normed = bonsai_vae_group_norm_nchw(
        input,
        2,
        norm_weight,
        norm_bias,
        1e-6F
    );
    BonsaiNchwTensor activated = normed;
    activated.values = bonsai_silu(normed.values);

    std::vector<float> weight;
    weight.reserve(2 * 4 * 3 * 3);
    for (uint64_t index = 0; index < 2 * 4 * 3 * 3; index++) {
        weight.push_back((static_cast<float>(index % 11U) - 5.0F) * 0.02F);
    }
    const std::vector<float> bias {0.05F, -0.025F};
    const BonsaiNchwTensor conv = bonsai_vae_conv2d_nchw(
        activated,
        weight,
        2,
        3,
        3,
        1,
        &bias
    );
    const BonsaiNchwTensor upsampled = bonsai_vae_upsample_nearest2x_nchw(conv);
    if (conv.height != 3 || conv.width != 3 || upsampled.height != 6 || upsampled.width != 6) {
        throw std::runtime_error("Bonsai VAE op shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : upsampled.values) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double vae_attention_checksum() {
    BonsaiNchwTensor queries {
        1,
        4,
        2,
        3,
        synthetic_input(1 * 4 * 2 * 3),
    };
    BonsaiNchwTensor keys {
        1,
        4,
        2,
        3,
        synthetic_input(1 * 4 * 2 * 3),
    };
    BonsaiNchwTensor values {
        1,
        4,
        2,
        3,
        synthetic_input(1 * 4 * 2 * 3),
    };
    const BonsaiNchwTensor attended = bonsai_vae_spatial_attention_nchw(
        queries,
        keys,
        values,
        1.0F / std::sqrt(4.0F)
    );
    const BonsaiNchwTensor residual = bonsai_vae_add_nchw(queries, attended);

    double checksum = 0.0;
    for (float value : residual.values) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

double vae_decode_prelude_checksum() {
    BonsaiNchwTensor packed {
        1,
        8,
        2,
        2,
        synthetic_input(1 * 8 * 2 * 2),
    };
    std::vector<float> mean;
    std::vector<float> variance;
    mean.reserve(8);
    variance.reserve(8);
    for (uint64_t index = 0; index < 8; index++) {
        mean.push_back((static_cast<float>(index) - 4.0F) * 0.01F);
        variance.push_back(0.75F + 0.05F * static_cast<float>(index));
    }

    const BonsaiNchwTensor denormalized = bonsai_vae_denormalize_channels_nchw(
        packed,
        mean,
        variance,
        1e-6F
    );
    const BonsaiNchwTensor unpatchified = bonsai_vae_unpatchify_nchw(denormalized);
    if (unpatchified.channels != 2 || unpatchified.height != 4 || unpatchified.width != 4) {
        throw std::runtime_error("Bonsai VAE decode prelude shape mismatch.");
    }

    double checksum = 0.0;
    for (float value : unpatchified.values) {
        checksum += static_cast<double>(value);
    }
    return checksum;
}

uint64_t validate_text_encoder_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    double* dequant_checksum,
    double* matvec_checksum,
    double* linear_checksum,
    double* embedding_checksum,
    double* norm_checksum,
    double* qwen_checksum,
    uint64_t* qwen_layers,
    uint64_t* qwen_logical_tensors,
    uint64_t* qwen_view_layers,
    uint64_t* qwen_view_bytes
) {
    uint64_t bytes = 0;
    const std::string embedding_key = tensors.resolve_model_prefixed_key("embed_tokens.weight");
    bytes += require_embedding_views(
        storage,
        tensors,
        tensors.require_packed_weight(embedding_key, 4, 64),
        embedding_checksum
    );
    bytes += require_rms_norm_view(
        storage,
        tensors,
        tensors.resolve_model_prefixed_key("norm.weight"),
        norm_checksum
    );
    const BonsaiQwenInventorySummary inventory = bonsai_require_qwen_text_encoder_tensors(
        tensors
    );
    *qwen_layers = inventory.layer_count;
    *qwen_logical_tensors = inventory.logical_tensor_count;
    const BonsaiQwenLayerProbeSummary qwen = bonsai_probe_qwen_text_encoder_layer0(
        storage,
        tensors
    );
    bytes += qwen.bytes;
    *qwen_checksum += qwen.checksum;
    const BonsaiQwenTextEncoderViews qwen_views = bonsai_require_qwen_text_encoder_views(
        storage,
        tensors
    );
    *qwen_view_layers = static_cast<uint64_t>(qwen_views.layers.size());
    *qwen_view_bytes = bonsai_qwen_text_encoder_byte_count(qwen_views);
    bytes += *qwen_view_bytes;
    return bytes;
}

uint64_t validate_transformer_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiQuantizationConfig& quantization,
    double* dequant_checksum,
    double* matvec_checksum,
    double* dense_checksum,
    double* linear_checksum,
    double* linear_sequence_checksum,
    uint64_t* flux_double_view_blocks,
    uint64_t* flux_single_view_blocks,
    uint64_t* flux_transformer_view_bytes
) {
    uint64_t bytes = 0;
    const BonsaiLinearViews x_embedder = bonsai_require_dense_linear_views(
        storage,
        tensors,
        "x_embedder.weight",
        "x_embedder.bias"
    );
    const std::vector<float> x_input = synthetic_input(x_embedder.input_values);
    *dense_checksum += static_cast<double>(bonsai_dense_matvec_row(
        x_embedder.dense,
        x_input,
        0
    ));
    *linear_checksum += static_cast<double>(bonsai_linear_row(
        x_embedder,
        x_input,
        0
    ));
    const std::vector<float> sequence_output = bonsai_linear_sequence(
        x_embedder,
        synthetic_input(2 * x_embedder.input_values),
        1,
        2
    );
    const size_t sequence_limit = std::min<size_t>(sequence_output.size(), 128);
    for (size_t index = 0; index < sequence_limit; index++) {
        *linear_sequence_checksum += static_cast<double>(sequence_output[index]);
    }
    bytes += bonsai_linear_byte_count(x_embedder);

    bytes += require_dense_weight_view(
        storage,
        tensors,
        "context_embedder.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "norm_out.linear.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "proj_out.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "time_guidance_embed.timestep_embedder.linear_1.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "time_guidance_embed.timestep_embedder.linear_2.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "double_stream_modulation_img.linear.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "double_stream_modulation_txt.linear.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_dense_weight_view(
        storage,
        tensors,
        "single_stream_modulation.linear.weight",
        dense_checksum,
        linear_checksum
    );
    bytes += require_packed_weight_views(
        storage,
        tensors,
        tensors.require_packed_weight(
            "transformer_blocks.0.attn.to_q.weight",
            quantization.bits,
            quantization.group_size
        ),
        dequant_checksum,
        matvec_checksum,
        linear_checksum
    );
    bytes += require_packed_weight_views(
        storage,
        tensors,
        tensors.require_packed_weight(
            "single_transformer_blocks.0.attn.to_qkv_mlp_proj.weight",
            quantization.bits,
            quantization.group_size
        ),
        dequant_checksum,
        matvec_checksum,
        linear_checksum
    );
    const BonsaiFluxTransformerViews transformer_views = bonsai_require_flux_transformer_views(
        storage,
        tensors,
        quantization.bits,
        quantization.group_size
    );
    *flux_double_view_blocks = static_cast<uint64_t>(transformer_views.double_blocks.size());
    *flux_single_view_blocks = static_cast<uint64_t>(transformer_views.single_blocks.size());
    *flux_transformer_view_bytes = bonsai_flux_transformer_byte_count(transformer_views);
    bytes += *flux_transformer_view_bytes;
    return bytes;
}

uint64_t validate_vae_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& tensors,
    const BonsaiFluxVaeConfig& config,
    double* tensor_vector_checksum,
    double* vae_conv_view_checksum,
    double* vae_group_norm_view_checksum,
    double* vae_attention_view_checksum,
    double* vae_resnet_view_checksum,
    double* vae_up_block_view_checksum,
    double* vae_decode_prelude_view_checksum
) {
    uint64_t bytes = 0;
    const BonsaiTensorView mean = storage.require_view(tensors, "bn.running_mean");
    const BonsaiTensorView variance = storage.require_view(tensors, "bn.running_var");
    const std::vector<float> mean_values = bonsai_tensor_view_to_f32_vector(mean);
    const std::vector<float> variance_values = bonsai_tensor_view_to_f32_vector(variance);
    const size_t mean_limit = std::min<size_t>(mean_values.size(), 64);
    const size_t variance_limit = std::min<size_t>(variance_values.size(), 64);
    for (size_t index = 0; index < mean_limit; index++) {
        *tensor_vector_checksum += static_cast<double>(mean_values[index]);
    }
    for (size_t index = 0; index < variance_limit; index++) {
        *tensor_vector_checksum += static_cast<double>(variance_values[index]);
    }
    bytes += mean.byte_count;
    bytes += variance.byte_count;
    const BonsaiVaeConv2dViews post_quant_conv = bonsai_vae_require_conv2d_views(
        storage,
        tensors,
        "post_quant_conv"
    );
    const BonsaiNchwTensor conv_input {
        1,
        post_quant_conv.input_channels,
        post_quant_conv.kernel_height,
        post_quant_conv.kernel_width,
        synthetic_input(
            post_quant_conv.input_channels *
            post_quant_conv.kernel_height *
            post_quant_conv.kernel_width
        ),
    };
    const BonsaiNchwTensor conv_output = bonsai_vae_conv2d_view_nchw(
        conv_input,
        post_quant_conv
    );
    const size_t conv_limit = std::min<size_t>(conv_output.values.size(), 64);
    for (size_t index = 0; index < conv_limit; index++) {
        *vae_conv_view_checksum += static_cast<double>(conv_output.values[index]);
    }
    bytes += bonsai_vae_conv2d_byte_count(post_quant_conv);
    const BonsaiVaeGroupNormViews norm_out = bonsai_vae_require_group_norm_views(
        storage,
        tensors,
        "decoder.conv_norm_out",
        config.block_out_channels[0],
        config.norm_num_groups,
        1e-6F
    );
    const BonsaiNchwTensor norm_input {
        1,
        norm_out.channels,
        2,
        2,
        synthetic_input(norm_out.channels * 2 * 2),
    };
    const BonsaiNchwTensor norm_output = bonsai_vae_group_norm_view_nchw(
        norm_input,
        norm_out
    );
    const size_t norm_limit = std::min<size_t>(norm_output.values.size(), 64);
    for (size_t index = 0; index < norm_limit; index++) {
        *vae_group_norm_view_checksum += static_cast<double>(norm_output.values[index]);
    }
    bytes += bonsai_vae_group_norm_byte_count(norm_out);
    const BonsaiVaeAttentionViews mid_attention = bonsai_vae_require_attention_views(
        storage,
        tensors,
        "decoder.mid_block.attentions.0",
        config.block_out_channels.back(),
        config.norm_num_groups,
        1e-6F
    );
    const BonsaiNchwTensor attention_input {
        1,
        mid_attention.channels,
        1,
        1,
        synthetic_input(mid_attention.channels)
    };
    const BonsaiNchwTensor attention_output = bonsai_vae_attention_view_nchw(
        attention_input,
        mid_attention
    );
    const size_t attention_limit = std::min<size_t>(attention_output.values.size(), 64);
    for (size_t index = 0; index < attention_limit; index++) {
        *vae_attention_view_checksum += static_cast<double>(attention_output.values[index]);
    }
    bytes += bonsai_vae_attention_byte_count(mid_attention);
    const BonsaiVaeResnetViews mid_resnet = bonsai_vae_require_resnet_views(
        storage,
        tensors,
        "decoder.mid_block.resnets.0",
        config.block_out_channels.back(),
        config.block_out_channels.back(),
        config.norm_num_groups,
        1e-6F
    );
    const BonsaiNchwTensor resnet_input {
        1,
        mid_resnet.input_channels,
        1,
        1,
        synthetic_input(mid_resnet.input_channels)
    };
    const BonsaiNchwTensor resnet_output = bonsai_vae_resnet_view_nchw(
        resnet_input,
        mid_resnet
    );
    const size_t resnet_limit = std::min<size_t>(resnet_output.values.size(), 64);
    for (size_t index = 0; index < resnet_limit; index++) {
        *vae_resnet_view_checksum += static_cast<double>(resnet_output.values[index]);
    }
    bytes += bonsai_vae_resnet_byte_count(mid_resnet);

    const uint64_t last_up_block = config.block_out_channels_count - 1U;
    const uint64_t last_up_output_channels = config.block_out_channels.front();
    const uint64_t last_up_input_channels = config.block_out_channels_count == 1
        ? last_up_output_channels
        : config.block_out_channels[1];
    const BonsaiVaeUpBlockViews last_up = bonsai_vae_require_up_block_views(
        storage,
        tensors,
        "decoder.up_blocks." + std::to_string(last_up_block),
        last_up_input_channels,
        last_up_output_channels,
        config.layers_per_block + 1U,
        config.norm_num_groups,
        false,
        1e-6F
    );
    const BonsaiNchwTensor up_input {
        1,
        last_up.input_channels,
        1,
        1,
        synthetic_input(last_up.input_channels)
    };
    const BonsaiNchwTensor up_output = bonsai_vae_up_block_view_nchw(up_input, last_up);
    const size_t up_limit = std::min<size_t>(up_output.values.size(), 64);
    for (size_t index = 0; index < up_limit; index++) {
        *vae_up_block_view_checksum += static_cast<double>(up_output.values[index]);
    }
    bytes += bonsai_vae_up_block_byte_count(last_up);
    const BonsaiVaeDecodeViews decode_views = bonsai_vae_require_decode_views(
        storage,
        tensors,
        config
    );
    const BonsaiNchwTensor packed_input {
        1,
        decode_views.packed_channels,
        1,
        1,
        synthetic_input(decode_views.packed_channels)
    };
    const BonsaiNchwTensor prelude_output = bonsai_vae_decode_prelude_view_nchw(
        packed_input,
        decode_views
    );
    const size_t prelude_limit = std::min<size_t>(prelude_output.values.size(), 64);
    for (size_t index = 0; index < prelude_limit; index++) {
        *vae_decode_prelude_view_checksum += static_cast<double>(prelude_output.values[index]);
    }
    bytes += bonsai_vae_decode_byte_count(decode_views);
    bytes += require_tensor_view(storage, tensors, "decoder.conv_in.weight");
    return bytes;
}

} // namespace

std::string probe_bonsai_model(const BonsaiModelPaths& paths) {
    bonsai_require_directory(paths.root_path, "root");
    bonsai_require_directory(paths.tokenizer_path, "tokenizer");
    bonsai_require_directory(paths.scheduler_path, "scheduler");
    bonsai_require_file(bonsai_join_path(paths.tokenizer_path, "tokenizer.json"), "tokenizer");
    bonsai_require_file(
        bonsai_join_path(paths.tokenizer_path, "tokenizer_config.json"),
        "tokenizer config"
    );
    const BonsaiQuantizationConfig quantization = bonsai_read_quantization_config(
        paths.packed_transformer_path
    );
    const BonsaiFluxVaeConfig vae_config = bonsai_read_vae_config(paths.vae_path);
    const BonsaiTokenizerData tokenizer_data = bonsai_load_tokenizer_data(
        paths.tokenizer_path
    );
    const BonsaiTokenizerMetadata& tokenizer_metadata = tokenizer_data.metadata;
    const BonsaiSafetensorsIndex transformer = BonsaiSafetensorsIndex::load_directory(
        paths.packed_transformer_path,
        "transformer"
    );
    const BonsaiSafetensorsIndex text_encoder = BonsaiSafetensorsIndex::load_directory(
        paths.text_encoder_path,
        "text encoder"
    );
    const BonsaiSafetensorsIndex vae = BonsaiSafetensorsIndex::load_directory(
        paths.vae_path,
        "vae"
    );

    const BonsaiFluxTransformerInventorySummary transformer_inventory =
        require_transformer_tensors(transformer, quantization);
    require_text_encoder_tensors(text_encoder);
    const BonsaiFluxVaeInventorySummary vae_inventory = require_vae_tensors(vae, vae_config);

    const BonsaiTensorStorage transformer_storage(transformer);
    const BonsaiTensorStorage text_encoder_storage(text_encoder);
    const BonsaiTensorStorage vae_storage(vae);
    double dequant_checksum = 0.0;
    double matvec_checksum = 0.0;
    double dense_checksum = 0.0;
    double linear_checksum = 0.0;
    double linear_sequence_checksum = 0.0;
    double tensor_vector_checksum = 0.0;
    double vae_conv_view_checksum = 0.0;
    double vae_group_norm_view_checksum = 0.0;
    double vae_attention_view_checksum = 0.0;
    double vae_resnet_view_checksum = 0.0;
    double vae_up_block_view_checksum = 0.0;
    double vae_decode_prelude_view_checksum = 0.0;
    double embedding_checksum = 0.0;
    double norm_checksum = 0.0;
    double qwen_checksum = 0.0;
    uint64_t qwen_layers = 0;
    uint64_t qwen_logical_tensors = 0;
    uint64_t qwen_view_layers = 0;
    uint64_t qwen_view_bytes = 0;
    uint64_t flux_double_view_blocks = 0;
    uint64_t flux_single_view_blocks = 0;
    uint64_t flux_transformer_view_bytes = 0;
    const double activation_probe_checksum = activation_checksum();
    const double flux_activation_probe_checksum = flux_activation_checksum();
    const double flux_layer_norm_probe_checksum = flux_layer_norm_checksum();
    const double flux_time_embedding_probe_checksum = flux_time_embedding_checksum();
    const double rotary_probe_checksum = rotary_checksum();
    const double flux_pos_embed_probe_checksum = flux_pos_embed_checksum();
    const double flux_rope_probe_checksum = flux_rope_checksum();
    const double flux_modulation_probe_checksum = flux_modulation_checksum();
    const double flux_attention_layout_probe_checksum = flux_attention_layout_checksum();
    const double flux_single_block_probe_checksum = flux_single_block_checksum();
    const double flux_double_block_probe_checksum = flux_double_block_checksum();
    const double flux_output_probe_checksum = flux_output_checksum();
    const double attention_probe_checksum = attention_checksum();
    const double scheduler_probe_checksum = scheduler_checksum();
    const double latent_probe_checksum = latent_checksum();
    const double prompt_probe_checksum = prompt_checksum();
    const double vae_ops_probe_checksum = vae_ops_checksum();
    const double vae_attention_probe_checksum = vae_attention_checksum();
    const double vae_decode_prelude_probe_checksum = vae_decode_prelude_checksum();
    const uint64_t transformer_bytes = validate_transformer_views(
        transformer_storage,
        transformer,
        quantization,
        &dequant_checksum,
        &matvec_checksum,
        &dense_checksum,
        &linear_checksum,
        &linear_sequence_checksum,
        &flux_double_view_blocks,
        &flux_single_view_blocks,
        &flux_transformer_view_bytes
    );
    const uint64_t text_encoder_bytes = validate_text_encoder_views(
        text_encoder_storage,
        text_encoder,
        &dequant_checksum,
        &matvec_checksum,
        &linear_checksum,
        &embedding_checksum,
        &norm_checksum,
        &qwen_checksum,
        &qwen_layers,
        &qwen_logical_tensors,
        &qwen_view_layers,
        &qwen_view_bytes
    );
    const uint64_t vae_bytes = validate_vae_views(
        vae_storage,
        vae,
        vae_config,
        &tensor_vector_checksum,
        &vae_conv_view_checksum,
        &vae_group_norm_view_checksum,
        &vae_attention_view_checksum,
        &vae_resnet_view_checksum,
        &vae_up_block_view_checksum,
        &vae_decode_prelude_view_checksum
    );

    return "bits=" + std::to_string(quantization.bits) +
        " group_size=" + std::to_string(quantization.group_size) +
        " tokenizer_vocab=" + std::to_string(tokenizer_metadata.vocab_size) +
        " tokenizer_merges=" + std::to_string(tokenizer_metadata.merge_count) +
        " tokenizer_pad_id=" + std::to_string(tokenizer_metadata.pad_token_id) +
        " tokenizer_eos_id=" + std::to_string(tokenizer_metadata.eos_token_id) +
        " tokenizer_checksum=" +
            std::to_string(bonsai_tokenizer_data_checksum(tokenizer_data)) +
        " transformer_files=" + std::to_string(transformer.file_count()) +
        " transformer_tensors=" + std::to_string(transformer.tensor_count()) +
        " transformer_probe_bytes=" + std::to_string(transformer_bytes) +
        " transformer_double_blocks=" + std::to_string(transformer_inventory.double_block_count) +
        " transformer_single_blocks=" + std::to_string(transformer_inventory.single_block_count) +
        " flux_double_view_blocks=" + std::to_string(flux_double_view_blocks) +
        " flux_single_view_blocks=" + std::to_string(flux_single_view_blocks) +
        " flux_transformer_view_bytes=" + std::to_string(flux_transformer_view_bytes) +
        " transformer_logical_tensors=" +
            std::to_string(transformer_inventory.logical_tensor_count) +
        " text_encoder_files=" + std::to_string(text_encoder.file_count()) +
        " text_encoder_tensors=" + std::to_string(text_encoder.tensor_count()) +
        " text_encoder_probe_bytes=" + std::to_string(text_encoder_bytes) +
        " qwen_layers=" + std::to_string(qwen_layers) +
        " qwen_logical_tensors=" + std::to_string(qwen_logical_tensors) +
        " qwen_view_layers=" + std::to_string(qwen_view_layers) +
        " qwen_view_bytes=" + std::to_string(qwen_view_bytes) +
        " vae_files=" + std::to_string(vae.file_count()) +
        " vae_tensors=" + std::to_string(vae.tensor_count()) +
        " vae_probe_bytes=" + std::to_string(vae_bytes) +
        " vae_up_blocks=" + std::to_string(vae_inventory.up_block_count) +
        " vae_resnet_blocks=" + std::to_string(vae_inventory.resnet_block_count) +
        " vae_attention_blocks=" + std::to_string(vae_inventory.attention_block_count) +
        " vae_norm_groups=" + std::to_string(vae_config.norm_num_groups) +
        " vae_batch_norm_eps=" + std::to_string(vae_config.batch_norm_eps) +
        " vae_logical_tensors=" + std::to_string(vae_inventory.logical_tensor_count) +
        " dequant_checksum=" + std::to_string(dequant_checksum) +
        " matvec_checksum=" + std::to_string(matvec_checksum) +
        " dense_checksum=" + std::to_string(dense_checksum) +
        " linear_checksum=" + std::to_string(linear_checksum) +
        " linear_sequence_checksum=" + std::to_string(linear_sequence_checksum) +
        " tensor_vector_checksum=" + std::to_string(tensor_vector_checksum) +
        " vae_conv_view_checksum=" + std::to_string(vae_conv_view_checksum) +
        " vae_group_norm_view_checksum=" + std::to_string(vae_group_norm_view_checksum) +
        " vae_attention_view_checksum=" + std::to_string(vae_attention_view_checksum) +
        " vae_resnet_view_checksum=" + std::to_string(vae_resnet_view_checksum) +
        " vae_up_block_view_checksum=" + std::to_string(vae_up_block_view_checksum) +
        " vae_decode_prelude_view_checksum=" +
            std::to_string(vae_decode_prelude_view_checksum) +
        " embedding_checksum=" + std::to_string(embedding_checksum) +
        " norm_checksum=" + std::to_string(norm_checksum) +
        " qwen_checksum=" + std::to_string(qwen_checksum) +
        " activation_checksum=" + std::to_string(activation_probe_checksum) +
        " flux_activation_checksum=" + std::to_string(flux_activation_probe_checksum) +
        " flux_layer_norm_checksum=" + std::to_string(flux_layer_norm_probe_checksum) +
        " flux_time_embedding_checksum=" + std::to_string(flux_time_embedding_probe_checksum) +
        " rotary_checksum=" + std::to_string(rotary_probe_checksum) +
        " flux_pos_embed_checksum=" + std::to_string(flux_pos_embed_probe_checksum) +
        " flux_rope_checksum=" + std::to_string(flux_rope_probe_checksum) +
        " flux_modulation_checksum=" + std::to_string(flux_modulation_probe_checksum) +
        " flux_attention_layout_checksum=" +
            std::to_string(flux_attention_layout_probe_checksum) +
        " flux_single_block_checksum=" + std::to_string(flux_single_block_probe_checksum) +
        " flux_double_block_checksum=" + std::to_string(flux_double_block_probe_checksum) +
        " flux_output_checksum=" + std::to_string(flux_output_probe_checksum) +
        " attention_checksum=" + std::to_string(attention_probe_checksum) +
        " scheduler_checksum=" + std::to_string(scheduler_probe_checksum) +
        " latent_checksum=" + std::to_string(latent_probe_checksum) +
        " prompt_checksum=" + std::to_string(prompt_probe_checksum) +
        " vae_ops_checksum=" + std::to_string(vae_ops_probe_checksum) +
        " vae_attention_checksum=" + std::to_string(vae_attention_probe_checksum) +
        " vae_decode_prelude_checksum=" +
            std::to_string(vae_decode_prelude_probe_checksum);
}
