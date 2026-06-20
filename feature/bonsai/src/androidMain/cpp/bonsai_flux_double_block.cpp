#include "bonsai_flux_double_block.h"

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
        throw std::runtime_error(std::string("Bonsai Flux double block shape overflow: ") + label);
    }
    return left * right;
}

uint64_t checked_add(uint64_t left, uint64_t right, const char* label) {
    if (left > std::numeric_limits<uint64_t>::max() - right) {
        throw std::runtime_error(std::string("Bonsai Flux double block shape overflow: ") + label);
    }
    return left + right;
}

size_t checked_size_3(uint64_t first, uint64_t second, uint64_t third, const char* label) {
    const uint64_t first_second = checked_multiply(first, second, label);
    const uint64_t total = checked_multiply(first_second, third, label);
    if (total > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error(std::string("Bonsai Flux double block shape overflow: ") + label);
    }
    return static_cast<size_t>(total);
}

void require_positive(uint64_t value, const char* label) {
    if (value == 0) {
        throw std::runtime_error(std::string("Bonsai Flux double block value must be positive: ") + label);
    }
}

void require_finite_positive(float value, const char* label) {
    if (value <= 0.0F || !std::isfinite(value)) {
        throw std::runtime_error(std::string("Bonsai Flux double block value must be finite and positive: ") + label);
    }
}

void require_projection_size(
    const std::vector<float>& values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t dimensions,
    const char* label
) {
    if (values.size() != checked_size_3(batch, sequence_length, dimensions, label)) {
        throw std::runtime_error(std::string("Bonsai Flux double block projection shape mismatch: ") + label);
    }
}

std::vector<float> apply_flux_rope(
    const std::vector<float>& projection,
    const std::vector<float>& norm_weight,
    const std::vector<float>& cos_values,
    const std::vector<float>& sin_values,
    uint64_t batch,
    uint64_t sequence_length,
    uint64_t heads,
    uint64_t head_dimension,
    float epsilon
) {
    return bonsai_flux_apply_rms_norm_and_rope(
        bonsai_flux_sequence_to_heads(projection, batch, sequence_length, heads, head_dimension),
        norm_weight,
        cos_values,
        sin_values,
        checked_multiply(batch, heads, "batch heads"),
        sequence_length,
        head_dimension,
        epsilon
    );
}

} // namespace

BonsaiFluxDoubleBlockReferenceOutput bonsai_flux_double_block_reference(
    const std::vector<float>& text,
    const std::vector<float>& image,
    const std::vector<float>& text_modulation_values,
    const std::vector<float>& image_modulation_values,
    const std::vector<float>& text_query_projection,
    const std::vector<float>& text_key_projection,
    const std::vector<float>& text_value_projection,
    const std::vector<float>& image_query_projection,
    const std::vector<float>& image_key_projection,
    const std::vector<float>& image_value_projection,
    const std::vector<float>& text_attention_update,
    const std::vector<float>& image_attention_update,
    const std::vector<float>& text_mlp_update,
    const std::vector<float>& image_mlp_update,
    const std::vector<float>& text_query_norm_weight,
    const std::vector<float>& text_key_norm_weight,
    const std::vector<float>& image_query_norm_weight,
    const std::vector<float>& image_key_norm_weight,
    const std::vector<float>& text_cos_values,
    const std::vector<float>& text_sin_values,
    const std::vector<float>& image_cos_values,
    const std::vector<float>& image_sin_values,
    uint64_t batch,
    uint64_t text_sequence_length,
    uint64_t image_sequence_length,
    uint64_t heads,
    uint64_t head_dimension,
    float layer_norm_epsilon,
    float rms_norm_epsilon
) {
    require_positive(batch, "batch");
    require_positive(text_sequence_length, "text sequence");
    require_positive(image_sequence_length, "image sequence");
    require_positive(heads, "heads");
    require_positive(head_dimension, "head dimension");
    require_finite_positive(layer_norm_epsilon, "layer norm epsilon");
    require_finite_positive(rms_norm_epsilon, "rms norm epsilon");

    const uint64_t dimensions = checked_multiply(heads, head_dimension, "hidden dimensions");
    require_projection_size(text, batch, text_sequence_length, dimensions, "text");
    require_projection_size(image, batch, image_sequence_length, dimensions, "image");
    require_projection_size(
        text_attention_update,
        batch,
        text_sequence_length,
        dimensions,
        "text attention update"
    );
    require_projection_size(
        image_attention_update,
        batch,
        image_sequence_length,
        dimensions,
        "image attention update"
    );
    require_projection_size(text_mlp_update, batch, text_sequence_length, dimensions, "text mlp");
    require_projection_size(image_mlp_update, batch, image_sequence_length, dimensions, "image mlp");
    if (text_query_norm_weight.size() != static_cast<size_t>(head_dimension) ||
        text_key_norm_weight.size() != static_cast<size_t>(head_dimension) ||
        image_query_norm_weight.size() != static_cast<size_t>(head_dimension) ||
        image_key_norm_weight.size() != static_cast<size_t>(head_dimension)) {
        throw std::runtime_error("Bonsai Flux double block norm weight shape mismatch.");
    }

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
        text,
        text_modulation.shift_msa,
        text_modulation.scale_msa,
        batch,
        text_sequence_length,
        dimensions,
        layer_norm_epsilon
    );
    const std::vector<float> normalized_image_msa = bonsai_flux_apply_modulated_layer_norm(
        image,
        image_modulation.shift_msa,
        image_modulation.scale_msa,
        batch,
        image_sequence_length,
        dimensions,
        layer_norm_epsilon
    );

    const uint64_t combined_sequence_length = checked_add(
        text_sequence_length,
        image_sequence_length,
        "combined sequence"
    );
    const uint64_t batch_heads = checked_multiply(batch, heads, "batch heads");
    const std::vector<float> full_queries = bonsai_flux_concat_head_sequences(
        apply_flux_rope(
            text_query_projection,
            text_query_norm_weight,
            text_cos_values,
            text_sin_values,
            batch,
            text_sequence_length,
            heads,
            head_dimension,
            rms_norm_epsilon
        ),
        apply_flux_rope(
            image_query_projection,
            image_query_norm_weight,
            image_cos_values,
            image_sin_values,
            batch,
            image_sequence_length,
            heads,
            head_dimension,
            rms_norm_epsilon
        ),
        batch,
        heads,
        text_sequence_length,
        image_sequence_length,
        head_dimension
    );
    const std::vector<float> full_keys = bonsai_flux_concat_head_sequences(
        apply_flux_rope(
            text_key_projection,
            text_key_norm_weight,
            text_cos_values,
            text_sin_values,
            batch,
            text_sequence_length,
            heads,
            head_dimension,
            rms_norm_epsilon
        ),
        apply_flux_rope(
            image_key_projection,
            image_key_norm_weight,
            image_cos_values,
            image_sin_values,
            batch,
            image_sequence_length,
            heads,
            head_dimension,
            rms_norm_epsilon
        ),
        batch,
        heads,
        text_sequence_length,
        image_sequence_length,
        head_dimension
    );
    const std::vector<float> full_values = bonsai_flux_concat_head_sequences(
        bonsai_flux_sequence_to_heads(
            text_value_projection,
            batch,
            text_sequence_length,
            heads,
            head_dimension
        ),
        bonsai_flux_sequence_to_heads(
            image_value_projection,
            batch,
            image_sequence_length,
            heads,
            head_dimension
        ),
        batch,
        heads,
        text_sequence_length,
        image_sequence_length,
        head_dimension
    );
    const BonsaiFluxHeadSequenceParts attention_parts = bonsai_flux_split_head_sequences(
        bonsai_scaled_dot_product_attention(
            full_queries,
            full_keys,
            full_values,
            {},
            batch_heads,
            combined_sequence_length,
            head_dimension,
            1.0F / std::sqrt(static_cast<float>(head_dimension))
        ),
        batch,
        heads,
        text_sequence_length,
        image_sequence_length,
        head_dimension
    );
    const std::vector<float> attention_text = bonsai_flux_heads_to_sequence(
        attention_parts.first,
        batch,
        text_sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> attention_image = bonsai_flux_heads_to_sequence(
        attention_parts.second,
        batch,
        image_sequence_length,
        heads,
        head_dimension
    );
    const std::vector<float> text_after_attention = bonsai_flux_apply_gated_residual(
        text,
        text_attention_update,
        text_modulation.gate_msa,
        batch,
        text_sequence_length,
        dimensions
    );
    const std::vector<float> image_after_attention = bonsai_flux_apply_gated_residual(
        image,
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
        layer_norm_epsilon
    );
    const std::vector<float> normalized_image_mlp = bonsai_flux_apply_modulated_layer_norm(
        image_after_attention,
        image_modulation.shift_mlp,
        image_modulation.scale_mlp,
        batch,
        image_sequence_length,
        dimensions,
        layer_norm_epsilon
    );

    return BonsaiFluxDoubleBlockReferenceOutput {
        batch,
        text_sequence_length,
        image_sequence_length,
        dimensions,
        normalized_text_msa,
        normalized_image_msa,
        attention_text,
        attention_image,
        normalized_text_mlp,
        normalized_image_mlp,
        bonsai_flux_apply_gated_residual(
            text_after_attention,
            text_mlp_update,
            text_modulation.gate_mlp,
            batch,
            text_sequence_length,
            dimensions
        ),
        bonsai_flux_apply_gated_residual(
            image_after_attention,
            image_mlp_update,
            image_modulation.gate_mlp,
            batch,
            image_sequence_length,
            dimensions
        ),
    };
}
