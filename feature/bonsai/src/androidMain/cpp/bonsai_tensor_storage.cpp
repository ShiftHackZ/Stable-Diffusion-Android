#include "bonsai_tensor_storage.h"

#include <algorithm>
#include <fcntl.h>
#include <limits>
#include <stdexcept>
#include <string>
#include <sys/mman.h>
#include <sys/stat.h>
#include <unistd.h>

namespace {

uint64_t checked_multiply(uint64_t left, uint64_t right, const std::string& tensor_key) {
    if (left != 0 && right > std::numeric_limits<uint64_t>::max() / left) {
        throw std::runtime_error("Bonsai tensor shape is too large: " + tensor_key);
    }
    return left * right;
}

size_t checked_size_t(uint64_t value, const std::string& label) {
    if (value > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error("Bonsai " + label + " is too large for this runtime.");
    }
    return static_cast<size_t>(value);
}

} // namespace

class BonsaiMappedFile {
public:
    explicit BonsaiMappedFile(const std::string& path) : path_(path) {
        fd_ = open(path.c_str(), O_RDONLY | O_CLOEXEC);
        if (fd_ < 0) {
            throw std::runtime_error("could not open Bonsai safetensors file: " + path);
        }

        struct stat info {};
        if (fstat(fd_, &info) != 0 || !S_ISREG(info.st_mode)) {
            close(fd_);
            fd_ = -1;
            throw std::runtime_error("invalid Bonsai safetensors file: " + path);
        }

        size_ = static_cast<uint64_t>(info.st_size);
        if (size_ == 0) {
            close(fd_);
            fd_ = -1;
            throw std::runtime_error("empty Bonsai safetensors file: " + path);
        }

        size_t map_size = 0;
        try {
            map_size = checked_size_t(size_, "safetensors file");
        } catch (...) {
            close(fd_);
            fd_ = -1;
            throw;
        }

        mapped_ = mmap(
            nullptr,
            map_size,
            PROT_READ,
            MAP_PRIVATE,
            fd_,
            0
        );
        if (mapped_ == MAP_FAILED) {
            mapped_ = nullptr;
            close(fd_);
            fd_ = -1;
            throw std::runtime_error("could not map Bonsai safetensors file: " + path);
        }
    }

    ~BonsaiMappedFile() {
        if (mapped_ != nullptr) {
            munmap(mapped_, static_cast<size_t>(size_));
        }
        if (fd_ >= 0) {
            close(fd_);
        }
    }

    BonsaiMappedFile(const BonsaiMappedFile&) = delete;
    BonsaiMappedFile& operator=(const BonsaiMappedFile&) = delete;

    const uint8_t* data() const {
        return static_cast<const uint8_t*>(mapped_);
    }

    uint64_t size() const {
        return size_;
    }

private:
    std::string path_;
    int fd_ = -1;
    void* mapped_ = nullptr;
    uint64_t size_ = 0;
};

BonsaiTensorStorage::BonsaiTensorStorage(const BonsaiSafetensorsIndex& index) {
    for (const std::string& file : index.files()) {
        mapped_files_.emplace(file, std::make_unique<BonsaiMappedFile>(file));
    }
}

BonsaiTensorStorage::~BonsaiTensorStorage() = default;

BonsaiTensorStorage::BonsaiTensorStorage(BonsaiTensorStorage&&) noexcept = default;

BonsaiTensorStorage& BonsaiTensorStorage::operator=(BonsaiTensorStorage&&) noexcept = default;

std::vector<float> bonsai_tensor_view_to_f32_vector(const BonsaiTensorView& view) {
    if (view.descriptor == nullptr || view.data == nullptr) {
        throw std::runtime_error("Bonsai tensor view is empty.");
    }
    if (view.element_count > static_cast<uint64_t>(std::numeric_limits<size_t>::max())) {
        throw std::runtime_error("Bonsai tensor view is too large: " + view.descriptor->key);
    }

    std::vector<float> output;
    output.reserve(static_cast<size_t>(view.element_count));
    for (uint64_t index = 0; index < view.element_count; index++) {
        output.push_back(bonsai_read_scalar_as_f32(
            view.data + index * view.dtype_byte_count,
            view.dtype
        ));
    }
    return output;
}

BonsaiTensorView BonsaiTensorStorage::view(const BonsaiTensorDescriptor& descriptor) const {
    const auto found = mapped_files_.find(descriptor.file_path);
    if (found == mapped_files_.end()) {
        throw std::runtime_error("Bonsai tensor file is not mapped: " + descriptor.file_path);
    }

    const BonsaiMappedFile& file = *found->second;
    if (descriptor.data_start > descriptor.data_end || descriptor.data_end > file.size()) {
        throw std::runtime_error("Bonsai tensor data range is invalid: " + descriptor.key);
    }

    const uint64_t bytes = descriptor.data_end - descriptor.data_start;
    const uint64_t elements = bonsai_shape_element_count(descriptor.shape, descriptor.key);
    const uint64_t dtype_bytes = bonsai_dtype_byte_count(descriptor.dtype_type);
    const uint64_t expected_bytes = checked_multiply(elements, dtype_bytes, descriptor.key);
    if (expected_bytes != bytes) {
        throw std::runtime_error(
            "Bonsai tensor byte size mismatch: " +
            descriptor.key +
            " expected " +
            std::to_string(expected_bytes) +
            " got " +
            std::to_string(bytes)
        );
    }

    return BonsaiTensorView {
        &descriptor,
        file.data() + checked_size_t(descriptor.data_start, "tensor offset"),
        bytes,
        elements,
        dtype_bytes,
        descriptor.dtype_type,
    };
}

BonsaiTensorView BonsaiTensorStorage::require_view(
    const BonsaiSafetensorsIndex& index,
    const std::string& key
) const {
    return view(index.require(key));
}
