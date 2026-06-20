#include "bonsai_flux_single_block.h"

#include "bonsai_activation.h"
#include "bonsai_attention.h"
#include "bonsai_flux_attention_layout.h"
#include "bonsai_flux_modulation.h"
#include "bonsai_flux_rope.h"

#include <cmath>
#include <limits>
#include <stdexcept>
#include <string>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const char* label) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error(std::string("Bonsai Flux single block shape overflow: ") + label);
    }
    return left * right;
}

uint64_t checked_add(uint64_t left, uint64_t right, const char* label) {
    if (left > std::numeric_limits<uint64_t>::max() - right) {
        throw std::runtime_error(std::string("Bonsai Flux single block shape overflow: ") + label);
    }
    return left + right;
}

size_t checked_size_3(uint64_t first, uint64_t second, uint64_t third, const char* label) {
    const uint64_t first_second = checked_multiply(first, second, label);
    const uint64_t total = checked_multiply(first_second, third, label);
    if (total > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error(std::string("Bonsai Flux single block shape overflow: ") + label);
    }
    return static_cast<size_t>(total);
}

void require_positive(uint64_t value, const char* label) {
    if (value == 0) {
        throw std::runtime_error(std::string("Bonsai Flux single block value must be positive: ") + label);
    }
}

void require_finite_positive(float value, const char* label) {
    if (value <= 0.0F || !std::isfinite(value)) {
        throw std::runtime_error(std::string("Bonsai Flux single block value must be finite and positive: ") + label);
    }
}

} // namespace

BonsaiFluxSingleBlockReferenceOutput bonsai_flux_single_block_reference(
    const std::vector<float>& hidden,
    const std::vector<float>& modulation_values,
    const std::vector<float>& fused_projection,
    const std::vector<float>& projected_update,
    const std::vector<float>& norm_q_weight,
    const std::vector<float>& norm_k_weight,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension,
    uint64_t mlp_hidden_dimensions,
    float layer_norm_epsilon,
    float rms_norm_epsilon
) {
    require_positive(batch, "batch");
    require_positive(sequence_length, "sequence");
    require_positive(heads, "heads");
    require_positive(head_dimension, "head dimension");
    require_positive(mlp_hidden_dimensions, "mlp hidden");
    require_finite_positive(layer_norm_epsilon, "layer norm epsilon");
    require_finite_positive(rms_norm_epsilon, "rms norm epsilon");

    const uint64_t dimensions = checked_multiply(heads, head_dimension, "hidden dimensions");
    const size_t hidden_size = checked_size_3(batch, sequence_length, dimensions, "hidden");
    if (hidden.size() != hidden_size || projected_update.size() != hidden_size) {
        throw std::runtime_error("Bonsai Flux single block hidden/update shape mismatch.");
    }
    if (norm_q_weight.size() != static_cast<size_t>(head_dimension) ||
        norm_k_weight.size() != static_cast<size_t>(head_dimension)) {
        throw std::runtime_error("Bonsai Flux single block norm weight shape mismatch.");
    }

    const BonsaiFluxSingleModulation modulation = bonsai_flux_split_single_modulation(
        modulation_values,
        batch,
        dimensions
    );
    const std::vector<float> normed = bonsai_flux_apply_modulated_layer_norm(
        hidden,
        modulation.shift,
        modulation.scale,
        batch,
        sequence_length,
        dimensions,
        layer_norm_epsilon
    );
    const BonsaiFluxSingleProjectionParts parts = bonsai_flux_split_single_projection(
        fused_projection,
        batch,
        sequence_length,
        dimensions,
        mlp_hidden_dimensions
    );

    const uint64_t batch_heads = checked_multiply(batch, heads, "batch heads");
    const std::vector<float> query = bonsai_flux_apply_rms_norm_and_rope(
        bonsai_flux_sequence_to_heads(parts.query, batch, sequence_length, heads, head_dimension),
        norm_q_weight,
        cos_values,
        sin_values,
        batch_heads,
        sequence_length,
        head_dimension,
        rms_norm_epsilon
    );
    const std::vector<float> key = bonsai_flux_apply_rms_norm_and_rope(
        bonsai_flux_sequence_to_heads(parts.key, batch, sequence_length, heads, head_dimension),
        norm_k_weight,
        cos_values,
        sin_values,
        batch_heads,
        sequence_length,
        head_dimension,
        rms_norm_epsilon
    );
    const std::vector<float> value = bonsai_flux_sequence_to_heads(
        parts.value,
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> attended = bonsai_flux_heads_to_sequence(
        bonsai_scaled_dot_product_attention(
            query,
            key,
            value,
            {},
            batch_heads,
            sequence_length,
            head_dimension,
            1.0F / std::sqrt(static_cast<float>(head_dimension))
        ),
        batch,
        sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> mlp_output = bonsai_swiglu_last_dimension(
        parts.mlp_values,
        checked_multiply(mlp_hidden_dimensions, 2U, "mlp width")
    );
    const std::vector<float> out_projection_input = bonsai_flux_concat_last_dimension(
        attended,
        mlp_output,
        batch,
        sequence_length,
        dimensions,
        mlp_hidden_dimensions
    );

    return BonsaiFluxSingleBlockReferenceOutput {
        batch,
        sequence_length,
        dimensions,
        checked_add(dimensions, mlp_hidden_dimensions, "out projection input"),
        normed,
        out_projection_input,
        bonsai_flux_apply_gated_residual(
            hidden,
            projected_update,
            modulation.gate,
            batch,
            sequence_length,
            dimensions
        ),
    };
}
