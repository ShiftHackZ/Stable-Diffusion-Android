#pragma once

#include "bonsai_flux_vae.h"

#include <cstdint>
#include <string>
#include <vector>

struct BonsaiQuantizationConfig {
    int bits = 0;
    int group_size = 0;
};

std::string bonsai_join_path(const std::string& parent, const std::string& child);

bool bonsai_path_is_directory(const std::string& path);

bool bonsai_path_is_regular_file(const std::string& path);

void bonsai_require_directory(const std::string& path, const std::string& label);

void bonsai_require_file(const std::string& path, const std::string& label);

std::string bonsai_read_text_file(const std::string& path);

int bonsai_parse_json_int(const std::string& json, const std::string& key);

float bonsai_parse_json_float(const std::string& json, const std::string& key);

std::vector<uint64_t> bonsai_parse_json_uint_array_values(
    const std::string& json,
    const std::string& key
);

BonsaiQuantizationConfig bonsai_read_quantization_config(
    const std::string& packed_transformer_path
);

BonsaiFluxVaeConfig bonsai_read_vae_config(const std::string& vae_path);
