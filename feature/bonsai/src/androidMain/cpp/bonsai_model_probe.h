#pragma once

#include <string>

struct BonsaiModelPaths {
    std::string root_path;
    std::string packed_transformer_path;
    std::string text_encoder_path;
    std::string tokenizer_path;
    std::string vae_path;
    std::string scheduler_path;
};

std::string probe_bonsai_model(const BonsaiModelPaths& paths);
