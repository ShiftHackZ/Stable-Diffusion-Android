#pragma once

#include "bonsai_matmul.h"
#include "bonsai_packed_weight.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"

#include <cstdint>
#include <vector>

enum class BonsaiEmbeddingWeightKind {
    Dense,
    Packed,
};

struct BonsaiEmbeddingViews {
    BonsaiEmbeddingWeightKind kind = BonsaiEmbeddingWeightKind::Dense;
    BonsaiDenseWeightViews dense;
    BonsaiPackedWeightViews packed;
    uint64_t rows = 0;
    uint64_t dimensions = 0;
};

BonsaiEmbeddingViews bonsai_require_embedding_views(
    const BonsaiTensorStorage& storage,
    const BonsaiSafetensorsIndex& index,
    const BonsaiPackedWeightDescriptor& descriptor
);

std::vector<float> bonsai_embedding_row(
    const BonsaiEmbeddingViews& views,
    uint64_t token_id
);

std::vector<float> bonsai_embedding_lookup(
    const BonsaiEmbeddingViews& views,
    const std::vector<uint64_t>& token_ids
);

uint64_t bonsai_embedding_byte_count(const BonsaiEmbeddingViews& views);
