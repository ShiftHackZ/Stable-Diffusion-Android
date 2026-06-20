#include "bonsai_safetensors.h"

#include <cctype>
#include <dirent.h>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <string>
#include <sys/stat.h>
#include <vector>

namespace {

constexpr uint64_t MAX_SAFETENSORS_HEADER_BYTES = 128ULL * 1024ULL * 1024ULL;

struct DirectoryHandle {
    explicit DirectoryHandle(const std::string& path) : value(opendir(path.c_str())) {}

    ~DirectoryHandle() {
        if (value != nullptr) {
            closedir(value);
        }
    }

    DIR* value = nullptr;
};

bool ends_with(const std::string& value, const std::string& suffix) {
    return value.size() >= suffix.size() &&
        value.compare(value.size() - suffix.size(), suffix.size(), suffix) == 0;
}

bool stat_path(const std::string& path, struct stat* output) {
    return lstat(path.c_str(), output) == 0;
}

bool is_directory(const std::string& path) {
    struct stat info {};
    return stat_path(path, &info) && S_ISDIR(info.st_mode);
}

uint64_t file_size(const std::string& path) {
    struct stat info {};
    if (!stat_path(path, &info) || !S_ISREG(info.st_mode)) {
        throw std::runtime_error("missing Bonsai safetensors file: " + path);
    }
    return static_cast<uint64_t>(info.st_size);
}

std::string join_path(const std::string& parent, const std::string& child) {
    if (parent.empty() || parent.back() == '/') {
        return parent + child;
    }
    return parent + "/" + child;
}

void collect_safetensors(
    const std::string& directory,
    std::vector<std::string>* output
) {
    DirectoryHandle handle(directory);
    if (handle.value == nullptr) {
        throw std::runtime_error("could not read Bonsai directory: " + directory);
    }

    while (dirent* entry = readdir(handle.value)) {
        const std::string name(entry->d_name);
        if (name == "." || name == "..") {
            continue;
        }

        const std::string path = join_path(directory, name);
        struct stat info {};
        if (!stat_path(path, &info)) {
            continue;
        }

        if (S_ISDIR(info.st_mode)) {
            collect_safetensors(path, output);
        } else if (S_ISREG(info.st_mode) && ends_with(name, ".safetensors")) {
            output->push_back(path);
        }
    }
}

uint64_t read_little_endian_u64(const unsigned char* bytes) {
    uint64_t value = 0;
    for (int index = 7; index >= 0; index--) {
        value = (value << 8U) | bytes[index];
    }
    return value;
}

class JsonCursor {
public:
    explicit JsonCursor(const std::string& json) : json_(json) {}

    bool at_end() {
        skip_whitespace();
        return index_ >= json_.size();
    }

    bool consume(char expected) {
        skip_whitespace();
        if (index_ < json_.size() && json_[index_] == expected) {
            index_++;
            return true;
        }
        return false;
    }

    void expect(char expected) {
        if (!consume(expected)) {
            throw std::runtime_error("invalid Bonsai safetensors JSON header");
        }
    }

    std::string parse_string() {
        skip_whitespace();
        expect('"');
        std::string value;
        while (index_ < json_.size()) {
            const char current = json_[index_++];
            if (current == '"') {
                return value;
            }
            if (current != '\\') {
                value.push_back(current);
                continue;
            }
            if (index_ >= json_.size()) {
                throw std::runtime_error("invalid Bonsai safetensors string escape");
            }
            const char escaped = json_[index_++];
            switch (escaped) {
                case '"':
                case '\\':
                case '/':
                    value.push_back(escaped);
                    break;
                case 'b':
                    value.push_back('\b');
                    break;
                case 'f':
                    value.push_back('\f');
                    break;
                case 'n':
                    value.push_back('\n');
                    break;
                case 'r':
                    value.push_back('\r');
                    break;
                case 't':
                    value.push_back('\t');
                    break;
                case 'u':
                    if (index_ + 4 > json_.size()) {
                        throw std::runtime_error("invalid Bonsai safetensors unicode escape");
                    }
                    index_ += 4;
                    value.push_back('?');
                    break;
                default:
                    throw std::runtime_error("invalid Bonsai safetensors string escape");
            }
        }
        throw std::runtime_error("unterminated Bonsai safetensors string");
    }

    uint64_t parse_uint64() {
        skip_whitespace();
        const size_t start = index_;
        while (index_ < json_.size() &&
               std::isdigit(static_cast<unsigned char>(json_[index_])) != 0) {
            index_++;
        }
        if (start == index_) {
            throw std::runtime_error("invalid Bonsai safetensors integer");
        }
        return static_cast<uint64_t>(std::stoull(json_.substr(start, index_ - start)));
    }

    std::vector<uint64_t> parse_uint64_array() {
        std::vector<uint64_t> values;
        expect('[');
        if (consume(']')) {
            return values;
        }
        while (true) {
            values.push_back(parse_uint64());
            if (consume(']')) {
                return values;
            }
            expect(',');
        }
    }

    void skip_value() {
        skip_whitespace();
        if (index_ >= json_.size()) {
            throw std::runtime_error("invalid Bonsai safetensors JSON value");
        }

        const char current = json_[index_];
        if (current == '"') {
            parse_string();
            return;
        }
        if (current == '{') {
            skip_object();
            return;
        }
        if (current == '[') {
            skip_array();
            return;
        }
        if (std::isdigit(static_cast<unsigned char>(current)) != 0 || current == '-') {
            skip_number();
            return;
        }
        if (try_skip_literal("true") ||
            try_skip_literal("false") ||
            try_skip_literal("null")
        ) {
            return;
        }
        throw std::runtime_error("invalid Bonsai safetensors JSON literal");
    }

private:
    void skip_whitespace() {
        while (index_ < json_.size() &&
               std::isspace(static_cast<unsigned char>(json_[index_])) != 0) {
            index_++;
        }
    }

    void skip_object() {
        expect('{');
        if (consume('}')) {
            return;
        }
        while (true) {
            parse_string();
            expect(':');
            skip_value();
            if (consume('}')) {
                return;
            }
            expect(',');
        }
    }

    void skip_array() {
        expect('[');
        if (consume(']')) {
            return;
        }
        while (true) {
            skip_value();
            if (consume(']')) {
                return;
            }
            expect(',');
        }
    }

    void skip_number() {
        if (index_ < json_.size() && json_[index_] == '-') {
            index_++;
        }
        while (index_ < json_.size() &&
               std::isdigit(static_cast<unsigned char>(json_[index_])) != 0) {
            index_++;
        }
        if (index_ < json_.size() && json_[index_] == '.') {
            index_++;
            while (index_ < json_.size() &&
                   std::isdigit(static_cast<unsigned char>(json_[index_])) != 0) {
                index_++;
            }
        }
        if (index_ < json_.size() && (json_[index_] == 'e' || json_[index_] == 'E')) {
            index_++;
            if (index_ < json_.size() && (json_[index_] == '+' || json_[index_] == '-')) {
                index_++;
            }
            while (index_ < json_.size() &&
                   std::isdigit(static_cast<unsigned char>(json_[index_])) != 0) {
                index_++;
            }
        }
    }

    bool try_skip_literal(const std::string& literal) {
        if (json_.compare(index_, literal.size(), literal) == 0) {
            index_ += literal.size();
            return true;
        }
        return false;
    }

    const std::string& json_;
    size_t index_ = 0;
};

BonsaiTensorDescriptor parse_tensor_descriptor(
    JsonCursor* cursor,
    const std::string& key
) {
    BonsaiTensorDescriptor descriptor;
    descriptor.key = key;
    bool has_dtype = false;
    bool has_shape = false;
    bool has_offsets = false;

    cursor->expect('{');
    if (!cursor->consume('}')) {
        while (true) {
            const std::string property = cursor->parse_string();
            cursor->expect(':');
            if (property == "dtype") {
                descriptor.dtype = cursor->parse_string();
                has_dtype = true;
            } else if (property == "shape") {
                descriptor.shape = cursor->parse_uint64_array();
                has_shape = true;
            } else if (property == "data_offsets") {
                const std::vector<uint64_t> offsets = cursor->parse_uint64_array();
                if (offsets.size() != 2 || offsets[0] > offsets[1]) {
                    throw std::runtime_error(
                        "invalid Bonsai safetensors data_offsets for tensor: " + key
                    );
                }
                descriptor.data_start = offsets[0];
                descriptor.data_end = offsets[1];
                has_offsets = true;
            } else {
                cursor->skip_value();
            }
            if (cursor->consume('}')) {
                break;
            }
            cursor->expect(',');
        }
    }

    if (!has_dtype || !has_shape || !has_offsets) {
        throw std::runtime_error("invalid Bonsai safetensors metadata for tensor: " + key);
    }
    try {
        descriptor.dtype_type = bonsai_dtype_from_safetensors(descriptor.dtype);
    } catch (const std::runtime_error&) {
        throw std::runtime_error(
            "unsupported Bonsai safetensors dtype " + descriptor.dtype + " for tensor: " + key
        );
    }
    return descriptor;
}

std::vector<BonsaiTensorDescriptor> parse_safetensors_header(
    const std::string& header,
    uint64_t data_base_offset,
    uint64_t file_byte_count,
    const std::string& file_path
) {
    JsonCursor cursor(header);
    std::vector<BonsaiTensorDescriptor> descriptors;
    cursor.expect('{');
    if (!cursor.consume('}')) {
        while (true) {
            const std::string key = cursor.parse_string();
            cursor.expect(':');
            if (key == "__metadata__") {
                cursor.skip_value();
            } else {
                BonsaiTensorDescriptor descriptor = parse_tensor_descriptor(&cursor, key);
                if (data_base_offset + descriptor.data_end > file_byte_count) {
                    throw std::runtime_error(
                        "Bonsai safetensors tensor data exceeds file size: " + key
                    );
                }
                descriptor.data_start += data_base_offset;
                descriptor.data_end += data_base_offset;
                descriptor.file_path = file_path;
                descriptors.push_back(descriptor);
            }

            if (cursor.consume('}')) {
                break;
            }
            cursor.expect(',');
        }
    }
    if (!cursor.at_end()) {
        throw std::runtime_error("invalid trailing data in Bonsai safetensors header");
    }
    return descriptors;
}

std::vector<BonsaiTensorDescriptor> read_safetensors_file(const std::string& path) {
    std::ifstream input(path, std::ios::binary);
    if (!input) {
        throw std::runtime_error("could not read Bonsai safetensors file: " + path);
    }

    unsigned char header_length_bytes[8] {};
    input.read(reinterpret_cast<char*>(header_length_bytes), sizeof(header_length_bytes));
    if (input.gcount() != static_cast<std::streamsize>(sizeof(header_length_bytes))) {
        throw std::runtime_error("invalid Bonsai safetensors header: " + path);
    }

    const uint64_t header_length = read_little_endian_u64(header_length_bytes);
    if (header_length == 0 || header_length > MAX_SAFETENSORS_HEADER_BYTES) {
        throw std::runtime_error("unsupported Bonsai safetensors header length: " + path);
    }

    std::string header(static_cast<size_t>(header_length), '\0');
    input.read(header.data(), static_cast<std::streamsize>(header.size()));
    if (input.gcount() != static_cast<std::streamsize>(header.size())) {
        throw std::runtime_error("truncated Bonsai safetensors header: " + path);
    }

    const uint64_t data_base_offset = 8ULL + header_length;
    return parse_safetensors_header(header, data_base_offset, file_size(path), path);
}

bool has_suffix(const std::string& value, const std::string& suffix) {
    return value.size() >= suffix.size() &&
        value.compare(value.size() - suffix.size(), suffix.size(), suffix) == 0;
}

std::string key_prefix_for_weight(const std::string& weight_key) {
    if (!has_suffix(weight_key, ".weight")) {
        throw std::runtime_error("expected tensor key ending in .weight: " + weight_key);
    }
    return weight_key.substr(0, weight_key.size() - std::string(".weight").size());
}

} // namespace

BonsaiSafetensorsIndex BonsaiSafetensorsIndex::load_directory(
    const std::string& directory,
    const std::string& label
) {
    if (!is_directory(directory)) {
        throw std::runtime_error("missing Bonsai " + label + " directory: " + directory);
    }

    std::vector<std::string> files;
    collect_safetensors(directory, &files);
    std::sort(files.begin(), files.end());
    if (files.empty()) {
        throw std::runtime_error("no .safetensors files in Bonsai " + label + " directory");
    }

    BonsaiSafetensorsIndex index;
    index.files_ = files;
    for (const std::string& file : files) {
        index.append(file, read_safetensors_file(file));
    }
    if (index.descriptors_.empty()) {
        throw std::runtime_error("Bonsai " + label + " checkpoint has no tensor metadata");
    }
    return index;
}

bool BonsaiSafetensorsIndex::contains(const std::string& key) const {
    return index_.find(key) != index_.end();
}

const BonsaiTensorDescriptor& BonsaiSafetensorsIndex::require(const std::string& key) const {
    const BonsaiTensorDescriptor* descriptor = optional(key);
    if (descriptor == nullptr) {
        throw std::runtime_error("Bonsai checkpoint is missing tensor: " + key);
    }
    return *descriptor;
}

const BonsaiTensorDescriptor* BonsaiSafetensorsIndex::optional(const std::string& key) const {
    const auto found = index_.find(key);
    if (found == index_.end()) {
        return nullptr;
    }
    return &descriptors_[found->second];
}

std::string BonsaiSafetensorsIndex::resolve_model_prefixed_key(
    const std::string& suffix
) const {
    if (contains(suffix)) {
        return suffix;
    }
    return "model." + suffix;
}

BonsaiPackedWeightDescriptor BonsaiSafetensorsIndex::require_packed_weight(
    const std::string& weight_key,
    int bits,
    int group_size
) const {
    const std::string prefix = key_prefix_for_weight(weight_key);
    const std::string scales_key = prefix + ".scales";
    const BonsaiTensorDescriptor* scales = optional(scales_key);
    if (scales == nullptr) {
        require(weight_key);
        return BonsaiPackedWeightDescriptor {
            false,
            weight_key,
            "",
            "",
            bits,
            group_size,
        };
    }

    const std::string biases_key = prefix + ".biases";
    require(biases_key);
    const BonsaiTensorDescriptor& packed = require(weight_key);
    if (packed.dtype_type != BonsaiDType::U32) {
        throw std::runtime_error("packed tensor " + weight_key + " must be uint32");
    }

    return BonsaiPackedWeightDescriptor {
        true,
        weight_key,
        scales_key,
        biases_key,
        bits,
        group_size,
    };
}

size_t BonsaiSafetensorsIndex::tensor_count() const {
    return descriptors_.size();
}

size_t BonsaiSafetensorsIndex::file_count() const {
    return files_.size();
}

const std::vector<std::string>& BonsaiSafetensorsIndex::files() const {
    return files_;
}

const std::vector<BonsaiTensorDescriptor>& BonsaiSafetensorsIndex::descriptors() const {
    return descriptors_;
}

void BonsaiSafetensorsIndex::append(
    const std::string& file_path,
    std::vector<BonsaiTensorDescriptor> descriptors
) {
    for (BonsaiTensorDescriptor& descriptor : descriptors) {
        descriptor.file_path = file_path;
        descriptors_.push_back(descriptor);
        index_[descriptor.key] = descriptors_.size() - 1;
    }
}
