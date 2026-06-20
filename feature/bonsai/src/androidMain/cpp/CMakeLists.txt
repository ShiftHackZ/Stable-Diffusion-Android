cmake_minimum_required(VERSION 3.22.1)

project(sdai_bonsai_runtime LANGUAGES CXX)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

add_library(
    sdai_bonsai
    SHARED
    bonsai_activation.cpp
    bonsai_attention.cpp
    bonsai_dequant.cpp
    bonsai_embedding.cpp
    bonsai_flux_attention_layout.cpp
    bonsai_flux_double_block.cpp
    bonsai_flux_modulation.cpp
    bonsai_flux_output.cpp
    bonsai_flux_pos_embed.cpp
    bonsai_flux_rope.cpp
    bonsai_flux_single_block.cpp
    bonsai_flux_time_embedding.cpp
    bonsai_flux_transformer.cpp
    bonsai_flux_vae.cpp
    bonsai_image_encoder.cpp
    bonsai_jni.cpp
    bonsai_layer_norm.cpp
    bonsai_latents.cpp
    bonsai_linear.cpp
    bonsai_matmul.cpp
    bonsai_model_config.cpp
    bonsai_model_probe.cpp
    bonsai_norm.cpp
    bonsai_packed_weight.cpp
    bonsai_prompt.cpp
    bonsai_qwen.cpp
    bonsai_qwen_inputs.cpp
    bonsai_rotary.cpp
    bonsai_runtime.cpp
    bonsai_runtime_context.cpp
    bonsai_scheduler.cpp
    bonsai_safetensors.cpp
    bonsai_tensor.cpp
    bonsai_tensor_storage.cpp
    bonsai_tokenizer.cpp
    bonsai_vae_decoder.cpp
    bonsai_vae_ops.cpp
    bonsai_vulkan.cpp
)

target_link_libraries(
    sdai_bonsai
    PRIVATE
    android
    log
    vulkan
    z
)

target_link_options(
    sdai_bonsai
    PRIVATE
    "-Wl,-z,max-page-size=16384"
    "-Wl,-z,common-page-size=16384"
)

target_compile_options(
    sdai_bonsai
    PRIVATE
    -Wall
    -Wextra
    -Wno-unused-parameter
    $<$<CONFIG:Debug>:-O3>
    $<$<CONFIG:Debug>:-DNDEBUG>
    $<$<CONFIG:Debug>:-fno-math-errno>
)
