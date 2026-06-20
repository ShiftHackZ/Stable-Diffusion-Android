#include "bonsai_qwen.h"

#include "bonsai_activation.h"
#include "bonsai_attention.h"
#include "bonsai_linear.h"
#include "bonsai_norm.h"
#include "bonsai_rotary.h"

#include <android/log.h>

#include <algorithm>
#include <cmath>
#include <cstddef>
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>

namespace {

constexpr const char* LOG_TAG = "SDAI-Bonsai";
constexpr uint64_t QWEN_HIDDEN_SIZE = 2560;
constexpr uint64_t QWEN_ATTENTION_HEADS = 32;
constexpr uint64_t QWEN_KEY_VALUE_HEADS = 8;
constexpr uint64_t QWEN_HEAD_DIMENSION = 128;
constexpr uint64_t QWEN_LAYER_COUNT = 36;
constexpr int QWEN_BITS = 4;
constexpr int QWEN_GROUP_SIZE = 64;

std::vector<float> synthetic_input(uint64_t size) {
    std::vector<float> input;
    input.reserve(static_cast<size_t>(size));
    for (uint64_t index = 0; index < size; index++) {
        input.push_back((static_cast<float>(index % 11U) - 5.0F) * 0.0625F);
    }
    return input;
}

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai Qwen shape is too large: ") + label);
    }
    return left * right;
}

void add_bytes(uint64_t* bytes, uint64_t extra, const char* label) {
    if (*bytes > std::numeric_limits<uint64_t>::max() - extra) {
        throw std::runtime_error(std::string("Bonsai Qwen byte count overflow: ") + label);
    }
    *bytes += extra;
}

std::string qwen_key(
    const BonsaiSafetensorsIndex& index,
    const std::string& suffix
) {
    return index.resolve_model_prefixed_key(suffix);
}

void require_qwen_linear_tensor(
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    const std::string& name
) {
    index.require_packed_weight(
        qwen_key(index, prefix + "." + name + ".weight"),
        QWEN_BITS,
        QWEN_GROUP_SIZE
    );
}

void require_qwen_norm_tensor(
    const BonsaiSafetensorsIndex& index,
    const std::string& suffix
) {
    index.require(qwen_key(index, suffix));
}

BonsaiLinearViews require_qwen_linear(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& prefix,
    const std::string& name
) {
    const std::string weight_key = qwen_key(index, prefix + "." + name + ".weight");
    return bonsai_require_packed_linear_views(
        storage,
        index,
        index.require_packed_weight(weight_key, QWEN_BITS, QWEN_GROUP_SIZE),
        qwen_key(index, prefix + "." + name + ".bias")
    );
}

BonsaiRmsNormWeightViews require_qwen_norm(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const std::string& suffix
) {
    return bonsai_require_rms_norm_weight(storage, index, qwen_key(index, suffix));
}

void require_linear_shape(
    const BonsaiLinearViews& linear,
    uint64_t expected_input,
    uint64_t expected_output,
    const std::string& label
) {
    if (linear.input_values != expected_input || linear.output_rows != expected_output) {
        throw std::runtime_error("Bonsai Qwen linear shape mismatch: " + label);
    }
}

void require_norm_shape(
    const BonsaiRmsNormWeightViews& norm,
    uint64_t expected_dimensions,
    const std::string& label
) {
    if (norm.dimensions != expected_dimensions) {
        throw std::runtime_error("Bonsai Qwen norm shape mismatch: " + label);
    }
}

std::vector<float> add_vectors(
    const std::vector<float>& left,
    const std::vector<float>& right,
    const char* label
) {
    if (left.size() != right.size()) {
        throw std::runtime_error(std::string("Bonsai Qwen residual size mismatch: ") + label);
    }
    std::vector<float> output;
    output.reserve(left.size());
    for (size_t index = 0; index < left.size(); index++) {
        output.push_back(left[index] + right[index]);
    }
    return output;
}

std::vector<float> sequence_to_heads(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    const uint64_t hidden_size = checked_multiply(heads, head_dimension, "sequence heads");
    const uint64_t expected_size = checked_multiply(
        checked_multiply(batch, sequence_length, "sequence heads"),
        hidden_size,
        "sequence heads"
    );
    if (input.size() != static_cast<size_t>(expected_size)) {
        throw std::runtime_error("Bonsai Qwen sequence-to-heads input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(expected_size));
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t head = 0; head < heads; head++) {
            for (uint64_t token = 0; token < sequence_length; token++) {
                for (uint64_t column = 0; column < head_dimension; column++) {
                    const uint64_t source = ((batch_index * sequence_length + token) * hidden_size) +
                        head * head_dimension +
                        column;
                    output.push_back(input[static_cast<size_t>(source)]);
                }
            }
        }
    }
    return output;
}

std::vector<float> heads_to_sequence(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension
) {
    const uint64_t hidden_size = checked_multiply(heads, head_dimension, "heads sequence");
    const uint64_t expected_size = checked_multiply(
        checked_multiply(batch, sequence_length, "heads sequence"),
        hidden_size,
        "heads sequence"
    );
    if (input.size() != static_cast<size_t>(expected_size)) {
        throw std::runtime_error("Bonsai Qwen heads-to-sequence input size mismatch.");
    }

    std::vector<float> output(static_cast<size_t>(expected_size), 0.0F);
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t head = 0; head < heads; head++) {
            for (uint64_t token = 0; token < sequence_length; token++) {
                for (uint64_t column = 0; column < head_dimension; column++) {
                    const uint64_t source = (((batch_index * heads + head) * sequence_length + token) *
                        head_dimension) + column;
                    const uint64_t target = ((batch_index * sequence_length + token) * hidden_size) +
                        head * head_dimension +
                        column;
                    output[static_cast<size_t>(target)] = input[static_cast<size_t>(source)];
                }
            }
        }
    }
    return output;
}

std::vector<float> apply_rotary_to_batched_heads(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t heads,
    uint64_t sequence_length,
    uint64_t head_dimension
) {
    const uint64_t expected_size = checked_multiply(
        checked_multiply(checked_multiply(batch, heads, "qwen rotary"), sequence_length, "qwen rotary"),
        head_dimension,
        "qwen rotary"
    );
    if (input.size() != static_cast<size_t>(expected_size)) {
        throw std::runtime_error("Bonsai Qwen rotary input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(input.size());
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        for (uint64_t head = 0; head < heads; head++) {
            for (uint64_t token = 0; token < sequence_length; token++) {
                std::vector<float> head_values;
                head_values.reserve(static_cast<size_t>(head_dimension));
                const uint64_t offset = (((batch_index * heads + head) * sequence_length + token) *
                    head_dimension);
                for (uint64_t column = 0; column < head_dimension; column++) {
                    head_values.push_back(input[static_cast<size_t>(offset + column)]);
                }
                const std::vector<float> rotated = bonsai_apply_rotary_to_heads(
                    head_values,
                    head_dimension,
                    token,
                    1000000.0F
                );
                output.insert(output.end(), rotated.begin(), rotated.end());
            }
        }
    }
    return output;
}

std::vector<float> repeat_kv_heads_batched(
    const std::vector<float>& input,
    uint64_t batch,
    uint64_t key_value_heads,
    uint64_t repeats,
    uint64_t sequence_length,
    uint64_t head_dimension
) {
    const uint64_t batch_stride = checked_multiply(
        checked_multiply(key_value_heads, sequence_length, "qwen repeat kv"),
        head_dimension,
        "qwen repeat kv"
    );
    if (input.size() != static_cast<size_t>(batch_stride * batch)) {
        throw std::runtime_error("Bonsai Qwen repeat-KV input size mismatch.");
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(batch_stride * repeats * batch));
    for (uint64_t batch_index = 0; batch_index < batch; batch_index++) {
        const auto start = input.begin() + static_cast<std::ptrdiff_t>(batch_index * batch_stride);
        const auto end = start + static_cast<std::ptrdiff_t>(batch_stride);
        const std::vector<float> repeated = bonsai_repeat_kv_heads(
            std::vector<float>(start, end),
            key_value_heads,
            repeats,
            sequence_length,
            head_dimension
        );
        output.insert(output.end(), repeated.begin(), repeated.end());
    }
    return output;
}

std::vector<float> qwen_attention_sequence(
    const BonsaiQwenAttentionViews& views,
    const std::vector<float>& hidden_states,
    uint64_t batch,
    uint64_t sequence_length,
    const std::vector<float>& additive_attention_mask
) {
    const std::vector<float> query_sequence = bonsai_linear_sequence(
        views.q_proj,
        hidden_states,
        batch,
        sequence_length
    );
    const std::vector<float> key_sequence = bonsai_linear_sequence(
        views.k_proj,
        hidden_states,
        batch,
        sequence_length
    );
    const std::vector<float> value_sequence = bonsai_linear_sequence(
        views.v_proj,
        hidden_states,
        batch,
        sequence_length
    );

    std::vector<float> queries = sequence_to_heads(
        bonsai_rms_norm(query_sequence, views.q_norm, 1e-6F),
        batch,
        sequence_length,
        views.attention_heads,
        views.head_dimension
    );
    std::vector<float> keys = sequence_to_heads(
        bonsai_rms_norm(key_sequence, views.k_norm, 1e-6F),
        batch,
        sequence_length,
        views.key_value_heads,
        views.head_dimension
    );
    std::vector<float> values = sequence_to_heads(
        value_sequence,
        batch,
        sequence_length,
        views.key_value_heads,
        views.head_dimension
    );

    queries = apply_rotary_to_batched_heads(
        queries,
        batch,
        views.attention_heads,
        sequence_length,
        views.head_dimension
    );
    keys = apply_rotary_to_batched_heads(
        keys,
        batch,
        views.key_value_heads,
        sequence_length,
        views.head_dimension
    );
    keys = repeat_kv_heads_batched(
        keys,
        batch,
        views.key_value_heads,
        views.attention_heads / views.key_value_heads,
        sequence_length,
        views.head_dimension
    );
    values = repeat_kv_heads_batched(
        values,
        batch,
        views.key_value_heads,
        views.attention_heads / views.key_value_heads,
        sequence_length,
        views.head_dimension
    );

    const std::vector<float> attended = bonsai_scaled_dot_product_attention(
        queries,
        keys,
        values,
        additive_attention_mask,
        batch * views.attention_heads,
        sequence_length,
        views.head_dimension,
        views.scale
    );
    return bonsai_linear_sequence(
        views.o_proj,
        heads_to_sequence(
            attended,
            batch,
            sequence_length,
            views.attention_heads,
            views.head_dimension
        ),
        batch,
        sequence_length
    );
}

std::vector<float> qwen_mlp_sequence(
    const BonsaiQwenMlpViews& views,
    const std::vector<float>& hidden_states,
    uint64_t batch,
    uint64_t sequence_length
) {
    return bonsai_linear_sequence(
        views.down_proj,
        bonsai_silu_times(
            bonsai_linear_sequence(views.gate_proj, hidden_states, batch, sequence_length),
            bonsai_linear_sequence(views.up_proj, hidden_states, batch, sequence_length)
        ),
        batch,
        sequence_length
    );
}

double checksum_linear_rows(
    const BonsaiLinearViews& linear,
    uint64_t row_count
) {
    const std::vector<float> input = synthetic_input(linear.input_values);
    const uint64_t limit = std::min(row_count, linear.output_rows);
    double checksum = 0.0;
    for (uint64_t row = 0; row < limit; row++) {
        checksum += static_cast<double>(bonsai_linear_row(linear, input, row));
    }
    return checksum;
}

std::vector<float> sample_linear_rows(
    const BonsaiLinearViews& linear,
    uint64_t row_count
) {
    const std::vector<float> input = synthetic_input(linear.input_values);
    const uint64_t limit = std::min(row_count, linear.output_rows);
    std::vector<float> output;
    output.reserve(static_cast<size_t>(limit));
    for (uint64_t row = 0; row < limit; row++) {
        output.push_back(bonsai_linear_row(linear, input, row));
    }
    return output;
}

double checksum_norm(
    const BonsaiRmsNormWeightViews& norm
) {
    const std::vector<float> output = bonsai_rms_norm(
        synthetic_input(norm.dimensions),
        norm,
        1e-6F
    );
    const size_t limit = std::min<size_t>(output.size(), 32);
    double checksum = 0.0;
    for (size_t index = 0; index < limit; index++) {
        checksum += static_cast<double>(output[index]);
    }
    return checksum;
}

std::vector<uint64_t> checked_token_ids(
    const BonsaiQwenTextEncoderViews& views,
    const std::vector<int32_t>& input_ids
) {
    std::vector<uint64_t> token_ids;
    token_ids.reserve(input_ids.size());
    for (int32_t input_id : input_ids) {
        if (input_id < 0) {
            throw std::runtime_error("Bonsai Qwen token id must be non-negative.");
        }
        const uint64_t token_id = static_cast<uint64_t>(input_id);
        if (token_id >= views.embedding.rows) {
            throw std::runtime_error("Bonsai Qwen token id is outside embedding rows.");
        }
        token_ids.push_back(token_id);
    }
    return token_ids;
}

std::vector<float> qwen_additive_attention_mask(
    const std::vector<int32_t>& attention_mask,
    uint64_t sequence_length
) {
    if (attention_mask.size() != static_cast<size_t>(sequence_length)) {
        throw std::runtime_error("Bonsai Qwen attention mask length mismatch.");
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(checked_multiply(
        sequence_length,
        sequence_length,
        "qwen attention mask"
    )));
    for (uint64_t row = 0; row < sequence_length; row++) {
        for (uint64_t column = 0; column < sequence_length; column++) {
            const bool masked_future_token = column > row;
            const bool masked_padding_token =
                attention_mask[static_cast<size_t>(column)] != 1;
            output.push_back(
                masked_future_token || masked_padding_token
                    ? -std::numeric_limits<float>::infinity()
                    : 0.0F
            );
        }
    }
    return output;
}

std::vector<float> flatten_selected_states(
    const std::vector<std::vector<float>>& selected_states,
    uint64_t sequence_length,
    uint64_t hidden_size
) {
    if (selected_states.empty()) {
        throw std::runtime_error("Bonsai Qwen selected hidden states are empty.");
    }
    const uint64_t selected_count = static_cast<uint64_t>(selected_states.size());
    const uint64_t state_size = checked_multiply(
        sequence_length,
        hidden_size,
        "qwen selected state"
    );
    const uint64_t output_size = checked_multiply(
        state_size,
        selected_count,
        "qwen selected states"
    );
    for (const std::vector<float>& state : selected_states) {
        if (state.size() != static_cast<size_t>(state_size)) {
            throw std::runtime_error("Bonsai Qwen selected hidden state size mismatch.");
        }
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(output_size));
    for (uint64_t token = 0; token < sequence_length; token++) {
        for (const std::vector<float>& state : selected_states) {
            const uint64_t offset = token * hidden_size;
            output.insert(
                output.end(),
                state.begin() + static_cast<std::ptrdiff_t>(offset),
                state.begin() + static_cast<std::ptrdiff_t>(offset + hidden_size)
            );
        }
    }
    return output;
}

void log_qwen_layer_phase(const char* phase, uint64_t layer, uint64_t sequence_length) {
    __android_log_print(
        ANDROID_LOG_INFO,
        LOG_TAG,
        "phase=%s layer=%llu sequence=%llu",
        phase,
        static_cast<unsigned long long>(layer),
        static_cast<unsigned long long>(sequence_length)
    );
}

} // namespace

BonsaiQwenInventorySummary bonsai_require_qwen_text_encoder_tensors(
    const BonsaiSafetensorsIndex& index
) {
    BonsaiQwenInventorySummary summary {
        QWEN_LAYER_COUNT,
        0,
    };

    require_qwen_norm_tensor(index, "norm.weight");
    summary.logical_tensor_count++;

    for (uint64_t layer = 0; layer < QWEN_LAYER_COUNT; layer++) {
        const std::string prefix = "layers." + std::to_string(layer);
        require_qwen_norm_tensor(index, prefix + ".input_layernorm.weight");
        require_qwen_norm_tensor(index, prefix + ".post_attention_layernorm.weight");
        require_qwen_norm_tensor(index, prefix + ".self_attn.q_norm.weight");
        require_qwen_norm_tensor(index, prefix + ".self_attn.k_norm.weight");
        summary.logical_tensor_count += 4;

        require_qwen_linear_tensor(index, prefix + ".self_attn", "q_proj");
        require_qwen_linear_tensor(index, prefix + ".self_attn", "k_proj");
        require_qwen_linear_tensor(index, prefix + ".self_attn", "v_proj");
        require_qwen_linear_tensor(index, prefix + ".self_attn", "o_proj");
        require_qwen_linear_tensor(index, prefix + ".mlp", "gate_proj");
        require_qwen_linear_tensor(index, prefix + ".mlp", "up_proj");
        require_qwen_linear_tensor(index, prefix + ".mlp", "down_proj");
        summary.logical_tensor_count += 7;
    }

    return summary;
}

BonsaiQwenLayerViews bonsai_require_qwen_layer_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    uint64_t layer
) {
    if (layer >= QWEN_LAYER_COUNT) {
        throw std::runtime_error("Bonsai Qwen layer index is out of range.");
    }
    const std::string prefix = "layers." + std::to_string(layer);
    BonsaiQwenLayerViews views {
        require_qwen_norm(storage, index, prefix + ".input_layernorm.weight"),
        require_qwen_norm(storage, index, prefix + ".post_attention_layernorm.weight"),
        {},
        {},
        QWEN_HIDDEN_SIZE,
    };
    views.attention = BonsaiQwenAttentionViews {
        require_qwen_linear(storage, index, prefix + ".self_attn", "q_proj"),
        require_qwen_linear(storage, index, prefix + ".self_attn", "k_proj"),
        require_qwen_linear(storage, index, prefix + ".self_attn", "v_proj"),
        require_qwen_linear(storage, index, prefix + ".self_attn", "o_proj"),
        require_qwen_norm(storage, index, prefix + ".self_attn.q_norm.weight"),
        require_qwen_norm(storage, index, prefix + ".self_attn.k_norm.weight"),
        QWEN_HIDDEN_SIZE,
        QWEN_ATTENTION_HEADS,
        QWEN_KEY_VALUE_HEADS,
        QWEN_HEAD_DIMENSION,
        1.0F / std::sqrt(static_cast<float>(QWEN_HEAD_DIMENSION)),
    };
    views.mlp = BonsaiQwenMlpViews {
        require_qwen_linear(storage, index, prefix + ".mlp", "gate_proj"),
        require_qwen_linear(storage, index, prefix + ".mlp", "up_proj"),
        require_qwen_linear(storage, index, prefix + ".mlp", "down_proj"),
        QWEN_HIDDEN_SIZE,
        0,
    };
    views.mlp.intermediate_size = views.mlp.gate_proj.output_rows;

    require_norm_shape(views.input_norm, QWEN_HIDDEN_SIZE, "input_layernorm");
    require_norm_shape(views.post_attention_norm, QWEN_HIDDEN_SIZE, "post_attention_layernorm");
    require_norm_shape(views.attention.q_norm, QWEN_HEAD_DIMENSION, "q_norm");
    require_norm_shape(views.attention.k_norm, QWEN_HEAD_DIMENSION, "k_norm");
    require_linear_shape(
        views.attention.q_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_ATTENTION_HEADS * QWEN_HEAD_DIMENSION,
        "q_proj"
    );
    require_linear_shape(
        views.attention.k_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_KEY_VALUE_HEADS * QWEN_HEAD_DIMENSION,
        "k_proj"
    );
    require_linear_shape(
        views.attention.v_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_KEY_VALUE_HEADS * QWEN_HEAD_DIMENSION,
        "v_proj"
    );
    require_linear_shape(
        views.attention.o_proj,
        QWEN_ATTENTION_HEADS * QWEN_HEAD_DIMENSION,
        QWEN_HIDDEN_SIZE,
        "o_proj"
    );
    require_linear_shape(
        views.mlp.gate_proj,
        QWEN_HIDDEN_SIZE,
        views.mlp.intermediate_size,
        "gate_proj"
    );
    require_linear_shape(
        views.mlp.up_proj,
        QWEN_HIDDEN_SIZE,
        views.mlp.intermediate_size,
        "up_proj"
    );
    require_linear_shape(
        views.mlp.down_proj,
        views.mlp.intermediate_size,
        QWEN_HIDDEN_SIZE,
        "down_proj"
    );
    return views;
}

BonsaiQwenTextEncoderViews bonsai_require_qwen_text_encoder_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index
) {
    const std::string embedding_key = qwen_key(index, "embed_tokens.weight");
    BonsaiQwenTextEncoderViews views {
        bonsai_require_embedding_views(
            storage,
            index,
            index.require_packed_weight(embedding_key, QWEN_BITS, QWEN_GROUP_SIZE)
        ),
        require_qwen_norm(storage, index, "norm.weight"),
        {},
        {9, 18, 27},
        QWEN_HIDDEN_SIZE,
    };
    if (views.embedding.dimensions != QWEN_HIDDEN_SIZE ||
        views.final_norm.dimensions != QWEN_HIDDEN_SIZE) {
        throw std::runtime_error("Bonsai Qwen text encoder top-level shape mismatch.");
    }
    views.layers.reserve(static_cast<size_t>(QWEN_LAYER_COUNT));
    for (uint64_t layer = 0; layer < QWEN_LAYER_COUNT; layer++) {
        views.layers.push_back(bonsai_require_qwen_layer_views(storage, index, layer));
    }
    return views;
}

std::vector<float> bonsai_qwen_layer_sequence(
    const BonsaiQwenLayerViews& views,
    const std::vector<float>& hidden_states,
    uint64_t batch,
    uint64_t sequence_length,
    const std::vector<float>& additive_attention_mask
) {
    if (batch == 0 || sequence_length == 0 || views.hidden_size == 0) {
        throw std::runtime_error("Bonsai Qwen layer sequence shape must be positive.");
    }
    const uint64_t expected_input = checked_multiply(
        checked_multiply(batch, sequence_length, "qwen layer sequence"),
        views.hidden_size,
        "qwen layer sequence"
    );
    if (hidden_states.size() != static_cast<size_t>(expected_input)) {
        throw std::runtime_error("Bonsai Qwen layer sequence input size mismatch.");
    }

    const std::vector<float> attended = qwen_attention_sequence(
        views.attention,
        bonsai_rms_norm(hidden_states, views.input_norm, 1e-6F),
        batch,
        sequence_length,
        additive_attention_mask
    );
    const std::vector<float> after_attention = add_vectors(
        hidden_states,
        attended,
        "attention"
    );
    return add_vectors(
        after_attention,
        qwen_mlp_sequence(
            views.mlp,
            bonsai_rms_norm(after_attention, views.post_attention_norm, 1e-6F),
            batch,
            sequence_length
        ),
        "mlp"
    );
}

BonsaiQwenPromptEmbeddings bonsai_qwen_text_encoder_forward(
    const BonsaiQwenTextEncoderViews& views,
    const std::vector<int32_t>& input_ids,
    const std::vector<int32_t>& attention_mask
) {
    if (input_ids.empty()) {
        throw std::runtime_error("Bonsai Qwen input ids must not be empty.");
    }
    if (input_ids.size() != attention_mask.size()) {
        throw std::runtime_error("Bonsai Qwen input ids and mask length mismatch.");
    }
    if (views.hidden_size == 0 ||
        views.layers.empty() ||
        views.embedding.dimensions != views.hidden_size) {
        throw std::runtime_error("Bonsai Qwen text encoder views are incomplete.");
    }

    const uint64_t batch = 1;
    const uint64_t sequence_length = static_cast<uint64_t>(input_ids.size());
    std::vector<float> hidden_states = bonsai_embedding_lookup(
        views.embedding,
        checked_token_ids(views, input_ids)
    );
    const std::vector<float> additive_attention_mask = qwen_additive_attention_mask(
        attention_mask,
        sequence_length
    );

    std::vector<std::vector<float>> selected_states;
    selected_states.reserve(views.hidden_state_layers.size());
    for (uint64_t layer = 0; layer < static_cast<uint64_t>(views.layers.size()); layer++) {
        log_qwen_layer_phase("qwen_layer_start", layer + 1U, sequence_length);
        hidden_states = bonsai_qwen_layer_sequence(
            views.layers[static_cast<size_t>(layer)],
            hidden_states,
            batch,
            sequence_length,
            additive_attention_mask
        );
        log_qwen_layer_phase("qwen_layer_done", layer + 1U, sequence_length);
        const uint64_t state_index = layer + 1;
        if (std::find(
                views.hidden_state_layers.begin(),
                views.hidden_state_layers.end(),
                state_index
            ) != views.hidden_state_layers.end()) {
            selected_states.push_back(hidden_states);
        }
    }

    if (selected_states.size() != views.hidden_state_layers.size()) {
        throw std::runtime_error("Bonsai Qwen did not produce all selected hidden states.");
    }

    BonsaiQwenPromptEmbeddings embeddings;
    embeddings.batch = batch;
    embeddings.sequence_length = sequence_length;
    embeddings.hidden_size = checked_multiply(
        views.hidden_size,
        static_cast<uint64_t>(selected_states.size()),
        "qwen prompt embedding width"
    );
    embeddings.selected_layer_count = static_cast<uint64_t>(selected_states.size());
    embeddings.values = flatten_selected_states(
        selected_states,
        sequence_length,
        views.hidden_size
    );
    return embeddings;
}

uint64_t bonsai_qwen_layer_byte_count(const BonsaiQwenLayerViews& views) {
    uint64_t bytes = bonsai_rms_norm_byte_count(views.input_norm);
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.post_attention_norm), "post norm");
    add_bytes(&bytes, bonsai_linear_byte_count(views.attention.q_proj), "q proj");
    add_bytes(&bytes, bonsai_linear_byte_count(views.attention.k_proj), "k proj");
    add_bytes(&bytes, bonsai_linear_byte_count(views.attention.v_proj), "v proj");
    add_bytes(&bytes, bonsai_linear_byte_count(views.attention.o_proj), "o proj");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.attention.q_norm), "q norm");
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.attention.k_norm), "k norm");
    add_bytes(&bytes, bonsai_linear_byte_count(views.mlp.gate_proj), "gate proj");
    add_bytes(&bytes, bonsai_linear_byte_count(views.mlp.up_proj), "up proj");
    add_bytes(&bytes, bonsai_linear_byte_count(views.mlp.down_proj), "down proj");
    return bytes;
}

uint64_t bonsai_qwen_text_encoder_byte_count(const BonsaiQwenTextEncoderViews& views) {
    uint64_t bytes = bonsai_embedding_byte_count(views.embedding);
    add_bytes(&bytes, bonsai_rms_norm_byte_count(views.final_norm), "final norm");
    for (const BonsaiQwenLayerViews& layer : views.layers) {
        add_bytes(&bytes, bonsai_qwen_layer_byte_count(layer), "layer");
    }
    return bytes;
}

BonsaiQwenLayerProbeSummary bonsai_probe_qwen_text_encoder_layer0(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index
) {
    BonsaiQwenLayerProbeSummary summary;

    const BonsaiRmsNormWeightViews input_norm = require_qwen_norm(
        storage,
        index,
        "layers.0.input_layernorm.weight"
    );
    const BonsaiRmsNormWeightViews post_attention_norm = require_qwen_norm(
        storage,
        index,
        "layers.0.post_attention_layernorm.weight"
    );
    const BonsaiRmsNormWeightViews q_norm = require_qwen_norm(
        storage,
        index,
        "layers.0.self_attn.q_norm.weight"
    );
    const BonsaiRmsNormWeightViews k_norm = require_qwen_norm(
        storage,
        index,
        "layers.0.self_attn.k_norm.weight"
    );

    require_norm_shape(input_norm, QWEN_HIDDEN_SIZE, "input_layernorm");
    require_norm_shape(post_attention_norm, QWEN_HIDDEN_SIZE, "post_attention_layernorm");
    require_norm_shape(q_norm, QWEN_HEAD_DIMENSION, "q_norm");
    require_norm_shape(k_norm, QWEN_HEAD_DIMENSION, "k_norm");

    const BonsaiLinearViews q_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.self_attn",
        "q_proj"
    );
    const BonsaiLinearViews k_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.self_attn",
        "k_proj"
    );
    const BonsaiLinearViews v_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.self_attn",
        "v_proj"
    );
    const BonsaiLinearViews o_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.self_attn",
        "o_proj"
    );
    const BonsaiLinearViews gate_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.mlp",
        "gate_proj"
    );
    const BonsaiLinearViews up_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.mlp",
        "up_proj"
    );
    const BonsaiLinearViews down_proj = require_qwen_linear(
        storage,
        index,
        "layers.0.mlp",
        "down_proj"
    );

    require_linear_shape(
        q_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_ATTENTION_HEADS * QWEN_HEAD_DIMENSION,
        "q_proj"
    );
    require_linear_shape(
        k_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_KEY_VALUE_HEADS * QWEN_HEAD_DIMENSION,
        "k_proj"
    );
    require_linear_shape(
        v_proj,
        QWEN_HIDDEN_SIZE,
        QWEN_KEY_VALUE_HEADS * QWEN_HEAD_DIMENSION,
        "v_proj"
    );
    require_linear_shape(
        o_proj,
        QWEN_ATTENTION_HEADS * QWEN_HEAD_DIMENSION,
        QWEN_HIDDEN_SIZE,
        "o_proj"
    );
    require_linear_shape(gate_proj, QWEN_HIDDEN_SIZE, up_proj.output_rows, "gate_proj");
    require_linear_shape(up_proj, QWEN_HIDDEN_SIZE, gate_proj.output_rows, "up_proj");
    require_linear_shape(down_proj, gate_proj.output_rows, QWEN_HIDDEN_SIZE, "down_proj");

    summary.bytes += bonsai_rms_norm_byte_count(input_norm);
    summary.bytes += bonsai_rms_norm_byte_count(post_attention_norm);
    summary.bytes += bonsai_rms_norm_byte_count(q_norm);
    summary.bytes += bonsai_rms_norm_byte_count(k_norm);
    summary.bytes += bonsai_linear_byte_count(q_proj);
    summary.bytes += bonsai_linear_byte_count(k_proj);
    summary.bytes += bonsai_linear_byte_count(v_proj);
    summary.bytes += bonsai_linear_byte_count(o_proj);
    summary.bytes += bonsai_linear_byte_count(gate_proj);
    summary.bytes += bonsai_linear_byte_count(up_proj);
    summary.bytes += bonsai_linear_byte_count(down_proj);

    summary.checksum += checksum_norm(input_norm);
    summary.checksum += checksum_norm(post_attention_norm);
    summary.checksum += checksum_norm(q_norm);
    summary.checksum += checksum_norm(k_norm);
    summary.checksum += checksum_linear_rows(q_proj, 4);
    summary.checksum += checksum_linear_rows(k_proj, 4);
    summary.checksum += checksum_linear_rows(v_proj, 4);
    summary.checksum += checksum_linear_rows(o_proj, 4);
    summary.checksum += checksum_linear_rows(down_proj, 4);

    const std::vector<float> gate_values = sample_linear_rows(gate_proj, 8);
    const std::vector<float> up_values = sample_linear_rows(up_proj, 8);
    const std::vector<float> gated = bonsai_silu_times(gate_values, up_values);
    for (float value : gated) {
        summary.checksum += static_cast<double>(value);
    }

    return summary;
}
