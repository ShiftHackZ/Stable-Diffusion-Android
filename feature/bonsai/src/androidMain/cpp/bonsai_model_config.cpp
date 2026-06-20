#include "bonsai_model_config.h"

#include <cctype>
#include <fstream>
#include <sstream>
#include <stdexcept>
#include <sys/stat.h>

namespace {

bool stat_path(const std::string& path, struct stat* output) {
    return lstat(path.c_str(), output) == 0;
}

} // namespace

std::string bonsai_join_path(const std::string& parent, const std::string& child) {
    if (parent.empty() || parent.back() == '/') {
        return parent + child;
    }
    return parent + "/" + child;
}

bool bonsai_path_is_directory(const std::string& path) {
    struct stat info {};
    return stat_path(path, &info) && S_ISDIR(info.st_mode);
}

bool bonsai_path_is_regular_file(const std::string& path) {
    struct stat info {};
    return stat_path(path, &info) && S_ISREG(info.st_mode);
}

void bonsai_require_directory(const std::string& path, const std::string& label) {
    if (!bonsai_path_is_directory(path)) {
        throw std::runtime_error("missing Bonsai " + label + " directory: " + path);
    }
}

void bonsai_require_file(const std::string& path, const std::string& label) {
    if (!bonsai_path_is_regular_file(path)) {
        throw std::runtime_error("missing Bonsai " + label + " file: " + path);
    }
}

std::string bonsai_read_text_file(const std::string& path) {
    std::ifstream input(path);
    if (!input) {
        throw std::runtime_error("could not read Bonsai file: " + path);
    }
    std::ostringstream output;
    output << input.rdbuf();
    return output.str();
}

int bonsai_parse_json_int(const std::string& json, const std::string& key) {
    const std::string quoted_key = "\"" + key + "\"";
    const size_t key_index = json.find(quoted_key);
    if (key_index == std::string::npos) {
        throw std::runtime_error("missing " + key + " in Bonsai JSON config");
    }

    const size_t colon_index = json.find(':', key_index + quoted_key.size());
    if (colon_index == std::string::npos) {
        throw std::runtime_error("invalid Bonsai JSON config");
    }

    size_t value_index = colon_index + 1;
    while (value_index < json.size() &&
           std::isspace(static_cast<unsigned char>(json[value_index])) != 0) {
        value_index++;
    }

    const size_t start = value_index;
    while (value_index < json.size() &&
           std::isdigit(static_cast<unsigned char>(json[value_index])) != 0) {
        value_index++;
    }

    if (start == value_index) {
        throw std::runtime_error("invalid " + key + " in Bonsai JSON config");
    }

    return std::stoi(json.substr(start, value_index - start));
}

float bonsai_parse_json_float(const std::string& json, const std::string& key) {
    const std::string quoted_key = "\"" + key + "\"";
    const size_t key_index = json.find(quoted_key);
    if (key_index == std::string::npos) {
        throw std::runtime_error("missing " + key + " in Bonsai JSON config");
    }

    const size_t colon_index = json.find(':', key_index + quoted_key.size());
    if (colon_index == std::string::npos) {
        throw std::runtime_error("invalid Bonsai JSON config");
    }

    size_t value_index = colon_index + 1;
    while (value_index < json.size() &&
           std::isspace(static_cast<unsigned char>(json[value_index])) != 0) {
        value_index++;
    }

    const size_t start = value_index;
    while (value_index < json.size()) {
        const char value = json[value_index];
        if (std::isdigit(static_cast<unsigned char>(value)) == 0 &&
            value != '.' &&
            value != '-' &&
            value != '+' &&
            value != 'e' &&
            value != 'E') {
            break;
        }
        value_index++;
    }
    if (start == value_index) {
        throw std::runtime_error("invalid " + key + " in Bonsai JSON config");
    }

    return std::stof(json.substr(start, value_index - start));
}

std::vector<uint64_t> bonsai_parse_json_uint_array_values(
    const std::string& json,
    const std::string& key
) {
    const std::string quoted_key = "\"" + key + "\"";
    const size_t key_index = json.find(quoted_key);
    if (key_index == std::string::npos) {
        throw std::runtime_error("missing " + key + " in Bonsai config JSON");
    }

    const size_t colon_index = json.find(':', key_index + quoted_key.size());
    if (colon_index == std::string::npos) {
        throw std::runtime_error("invalid Bonsai config JSON");
    }

    size_t value_index = colon_index + 1;
    while (value_index < json.size() &&
           std::isspace(static_cast<unsigned char>(json[value_index])) != 0) {
        value_index++;
    }
    if (value_index >= json.size() || json[value_index] != '[') {
        throw std::runtime_error("invalid " + key + " in Bonsai config JSON");
    }
    value_index++;

    std::vector<uint64_t> values;
    while (value_index < json.size()) {
        while (value_index < json.size() &&
               std::isspace(static_cast<unsigned char>(json[value_index])) != 0) {
            value_index++;
        }
        if (value_index < json.size() && json[value_index] == ']') {
            break;
        }

        const size_t start = value_index;
        while (value_index < json.size() &&
               std::isdigit(static_cast<unsigned char>(json[value_index])) != 0) {
            value_index++;
        }
        if (start == value_index) {
            throw std::runtime_error("invalid " + key + " in Bonsai config JSON");
        }
        values.push_back(std::stoull(json.substr(start, value_index - start)));

        while (value_index < json.size() &&
               std::isspace(static_cast<unsigned char>(json[value_index])) != 0) {
            value_index++;
        }
        if (value_index < json.size() && json[value_index] == ',') {
            value_index++;
            continue;
        }
        if (value_index < json.size() && json[value_index] == ']') {
            break;
        }
        throw std::runtime_error("invalid " + key + " in Bonsai config JSON");
    }

    if (values.empty()) {
        throw std::runtime_error("empty " + key + " in Bonsai config JSON");
    }
    return values;
}

BonsaiQuantizationConfig bonsai_read_quantization_config(
    const std::string& packed_transformer_path
) {
    const std::string path = bonsai_join_path(
        packed_transformer_path,
        "quantization_config.json"
    );
    bonsai_require_file(path, "quantization config");
    const std::string json = bonsai_read_text_file(path);
    BonsaiQuantizationConfig config {
        bonsai_parse_json_int(json, "bits"),
        bonsai_parse_json_int(json, "group_size"),
    };

    if ((config.bits != 1 && config.bits != 2) || config.group_size != 128) {
        throw std::runtime_error(
            "Unsupported Bonsai quantization: " +
            std::to_string(config.bits) +
            "-bit group " +
            std::to_string(config.group_size) +
            "."
        );
    }

    return config;
}

BonsaiFluxVaeConfig bonsai_read_vae_config(const std::string& vae_path) {
    const std::string path = bonsai_join_path(vae_path, "config.json");
    bonsai_require_file(path, "vae config");
    const std::string json = bonsai_read_text_file(path);
    const std::vector<uint64_t> block_out_channels = bonsai_parse_json_uint_array_values(
        json,
        "block_out_channels"
    );
    BonsaiFluxVaeConfig config {
        static_cast<uint64_t>(block_out_channels.size()),
        static_cast<uint64_t>(bonsai_parse_json_int(json, "layers_per_block")),
        static_cast<uint64_t>(bonsai_parse_json_int(json, "norm_num_groups")),
        bonsai_parse_json_float(json, "batch_norm_eps"),
        block_out_channels,
    };
    if (config.layers_per_block == 0 ||
        config.block_out_channels_count != 4 ||
        config.norm_num_groups == 0 ||
        config.batch_norm_eps <= 0.0F) {
        throw std::runtime_error("invalid Bonsai VAE decoder config.");
    }
    return config;
}
