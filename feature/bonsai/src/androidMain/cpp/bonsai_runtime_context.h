#pragma once

#include "bonsai_flux_transformer.h"
#include "bonsai_flux_vae.h"
#include "bonsai_model_config.h"
#include "bonsai_model_probe.h"
#include "bonsai_qwen.h"
#include "bonsai_safetensors.h"
#include "bonsai_tensor_storage.h"
#include "bonsai_tokenizer.h"
#include "bonsai_vae_decoder.h"

#include <memory>
#include <string>

struct BonsaiTextEncoderRuntimeContext {
    explicit BonsaiTextEncoderRuntimeContext(const BonsaiModelPaths& model_paths);

    BonsaiModelPaths paths;
    BonsaiTokenizerData tokenizer_data;
    BonsaiSafetensorsIndex text_encoder_index;
    BonsaiTensorStorage text_encoder_storage;
    BonsaiQwenTextEncoderViews text_encoder_views;
    BonsaiQwenInventorySummary text_encoder_inventory;
    std::string summary;
};

struct BonsaiFluxTransformerRuntimeContext {
    explicit BonsaiFluxTransformerRuntimeContext(const BonsaiModelPaths& model_paths);

    BonsaiModelPaths paths;
    BonsaiQuantizationConfig quantization;
    BonsaiSafetensorsIndex transformer_index;
    BonsaiTensorStorage transformer_storage;
    BonsaiFluxTransformerViews transformer_views;
    BonsaiFluxTransformerInventorySummary transformer_inventory;
    std::string summary;
};

struct BonsaiVaeRuntimeContext {
    explicit BonsaiVaeRuntimeContext(const BonsaiModelPaths& model_paths);

    BonsaiModelPaths paths;
    BonsaiFluxVaeConfig vae_config;
    BonsaiSafetensorsIndex vae_index;
    BonsaiTensorStorage vae_storage;
    BonsaiVaeDecodeViews vae_views;
    BonsaiFluxVaeInventorySummary vae_inventory;
    std::string summary;
};

struct BonsaiRuntimeModelContext {
    explicit BonsaiRuntimeModelContext(const BonsaiModelPaths& model_paths);

    BonsaiModelPaths paths;
    BonsaiQuantizationConfig quantization;
    BonsaiFluxVaeConfig vae_config;
    BonsaiTokenizerData tokenizer_data;

    BonsaiSafetensorsIndex transformer_index;
    BonsaiSafetensorsIndex text_encoder_index;
    BonsaiSafetensorsIndex vae_index;

    BonsaiTensorStorage transformer_storage;
    BonsaiTensorStorage text_encoder_storage;
    BonsaiTensorStorage vae_storage;

    BonsaiFluxTransformerViews transformer_views;
    BonsaiQwenTextEncoderViews text_encoder_views;
    BonsaiVaeDecodeViews vae_views;

    BonsaiFluxTransformerInventorySummary transformer_inventory;
    BonsaiQwenInventorySummary text_encoder_inventory;
    BonsaiFluxVaeInventorySummary vae_inventory;

    std::string summary;
};

std::unique_ptr<BonsaiRuntimeModelContext> bonsai_load_runtime_model_context(
    const BonsaiModelPaths& paths
);

std::unique_ptr<BonsaiTextEncoderRuntimeContext> bonsai_load_text_encoder_runtime_context(
    const BonsaiModelPaths& paths
);

std::unique_ptr<BonsaiFluxTransformerRuntimeContext> bonsai_load_flux_transformer_runtime_context(
    const BonsaiModelPaths& paths
);

std::unique_ptr<BonsaiVaeRuntimeContext> bonsai_load_vae_runtime_context(
    const BonsaiModelPaths& paths
);
