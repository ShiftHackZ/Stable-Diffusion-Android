#pragma once

#include <atomic>
#include <functional>
#include <stdexcept>
#include <string>

#include "bonsai_model_probe.h"

struct BonsaiGenerationRequest {
    BonsaiModelPaths model_paths;
    std::string prompt;
    std::string negative_prompt;
    int sampling_steps = 0;
    float cfg_scale = 0.0F;
    int width = 0;
    int height = 0;
    std::string seed;
    int batch_count = 0;
    bool allow_nsfw = false;
    std::string backend = "auto";
};

class BonsaiGenerationCancelled : public std::runtime_error {
public:
    BonsaiGenerationCancelled();
};

using BonsaiProgressCallback = std::function<void(int current, int total)>;

std::string bonsai_generate_image(
    const BonsaiGenerationRequest& request,
    const BonsaiProgressCallback& progress_callback,
    const std::atomic_bool& cancel_requested
);
