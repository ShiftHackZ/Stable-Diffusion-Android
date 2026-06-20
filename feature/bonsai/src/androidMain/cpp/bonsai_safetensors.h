#pragma once

#include "bonsai_tensor.h"

#include <cstdint>
#include <string>
#include <unordered_map>
#include <vector>

struct BonsaiTensorDescriptor {
    std::string key;
    std::string file_path;
    std::string dtype;
    BonsaiDType dtype_type = BonsaiDType::F32;
    std::vector<uint64_t> shape;
    uint64_t data_start = 0;
    uint64_t data_end = 0;
};

struct BonsaiPackedWeightDescriptor {
    bool packed = false;
    std::string weight_key;
    std::string scales_key;
    std::string biases_key;
    int bits = 0;
    int group_size = 0;
};

class BonsaiSafetensorsIndex {
public:
    static BonsaiSafetensorsIndex load_directory(
        const std::string& directory,
        const std::string& label
    );

    bool contains(const std::string& key) const;
    const BonsaiTensorDescriptor& require(const std::string& key) const;
    const BonsaiTensorDescriptor* optional(const std::string& key) const;
    std::string resolve_model_prefixed_key(const std::string& suffix) const;
    BonsaiPackedWeightDescriptor require_packed_weight(
        const std::string& weight_key,
        int bits,
        int group_size
    ) const;

    size_t tensor_count() const;
    size_t file_count() const;
    const std::vector<std::string>& files() const;
    const std::vector<BonsaiTensorDescriptor>& descriptors() const;

private:
    void append(
        const std::string& file_path,
        std::vector<BonsaiTensorDescriptor> descriptors
    );

    std::vector<std::string> files_;
    std::vector<BonsaiTensorDescriptor> descriptors_;
    std::unordered_map<std::string, size_t> index_;
};
