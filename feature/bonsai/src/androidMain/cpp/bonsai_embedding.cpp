#include "bonsai_embedding.h"

#include "bonsai_dequant.h"
#include "bonsai_tensor.h"

#include <stdexcept>
#include <string>

namespace {

void require_token_id(const BonsaiEmbeddingViews& views, uint64_t token_id) {
    if (token_id >= views.rows) {
        throw std::runtime_error(
            "Bonsai embedding token id is out of range: " + std::to_string(token_id)
        );
    }
}

std::vector<float> dense_embedding_row(
    const BonsaiDenseWeightViews& views,
    uint64_t token_id
) {
    std::vector<float> output;
    output.reserve(static_cast<size_t>(views.input_values));
    for (uint64_t column = 0; column < views.input_values; column++) {
        const uint64_t index = token_id * views.input_values + column;
        output.push_back(
            bonsai_read_scalar_as_f32(
                views.weight.data + index * views.weight.dtype_byte_count,
                views.weight.dtype
            )
        );
    }
    return output;
}

} // namespace

BonsaiEmbeddingViews bonsai_require_embedding_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor
) {
    BonsaiEmbeddingViews views;
    views.kind = descriptor.packed ? BonsaiEmbeddingWeightKind::Packed : BonsaiEmbeddingWeightKind::Dense;
    if (descriptor.packed) {
        views.packed = bonsai_require_packed_weight_views(storage, index, descriptor);
        views.rows = views.packed.leading_rows;
        views.dimensions = views.packed.input_values;
    } else {
        views.dense = bonsai_require_dense_weight_view(storage, index, descriptor.weight_key);
        views.rows = views.dense.leading_rows;
        views.dimensions = views.dense.input_values;
    }
    return views;
}

std::vector<float> bonsai_embedding_row(
    const BonsaiEmbeddingViews& views,
    uint64_t token_id
) {
    require_token_id(views, token_id);
    switch (views.kind) {
        case BonsaiEmbeddingWeightKind::Dense:
            return dense_embedding_row(views.dense, token_id);
        case BonsaiEmbeddingWeightKind::Packed:
            return bonsai_dequantize_packed_row(views.packed, token_id);
    }
    throw std::runtime_error("Unsupported Bonsai embedding weight kind.");
}

std::vector<float> bonsai_embedding_lookup(
    const BonsaiEmbeddingViews& views,
    const std::vector<uint64_t>& token_ids
) {
    std::vector<float> output;
    output.reserve(static_cast<size_t>(views.dimensions * token_ids.size()));
    for (uint64_t token_id : token_ids) {
        const std::vector<float> row = bonsai_embedding_row(views, token_id);
        output.insert(output.end(), row.begin(), row.end());
    }
    return output;
}

uint64_t bonsai_embedding_byte_count(const BonsaiEmbeddingViews& views) {
    switch (views.kind) {
        case BonsaiEmbeddingWeightKind::Dense:
            return views.dense.weight.byte_count;
        case BonsaiEmbeddingWeightKind::Packed:
            return bonsai_packed_weight_byte_count(views.packed);
    }
    return 0;
}
