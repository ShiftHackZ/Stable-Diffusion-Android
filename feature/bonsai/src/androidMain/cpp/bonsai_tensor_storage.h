#pragma once

#include "bonsai_safetensors.h"

#include <cstdint>
#include <memory>
#include <string>
#include <unordered_map>
#include <vector>

class BonsaiMappedFile;

struct BonsaiTensorView {
    const BonsaiTensorDescriptor* descriptor = nullptr;
    const uint8_t* data = nullptr;
    uint64_t byte_count = 0;
    uint64_t element_count = 0;
    uint64_t dtype_byte_count = 0;
    BonsaiDType dtype = BonsaiDType::F32;
};

std::vector<float> bonsai_tensor_view_to_f32_vector(const BonsaiTensorView& view);

class BonsaiTensorStorage {
public:
    explicit BonsaiTensorStorage(const BonsaiSafetensorsIndex& index);
    ~BonsaiTensorStorage();

    BonsaiTensorStorage(const BonsaiTensorStorage&) = delete;
    BonsaiTensorStorage& operator=(const BonsaiTensorStorage&) = delete;
    BonsaiTensorStorage(BonsaiTensorStorage&&) noexcept;
    BonsaiTensorStorage& operator=(BonsaiTensorStorage&&) noexcept;

    BonsaiTensorView view(const BonsaiTensorDescriptor& descriptor) const;
    BonsaiTensorView require_view(
        const BonsaiSafetensorsIndex& index,
        const std::string& key
    ) const;

private:
    std::unordered_map<std::string, std::unique_ptr<BonsaiMappedFile>> mapped_files_;
};
