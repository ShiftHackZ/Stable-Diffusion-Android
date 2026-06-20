#include "bonsai_runtime_context.h"

#include <sstream>

namespace {

void require_runtime_layout(const BonsaiModelPaths& paths) {
    bonsai_require_directory(paths.root_path, "root");
    bonsai_require_directory(paths.tokenizer_path, "tokenizer");
    bonsai_require_directory(paths.scheduler_path, "scheduler");
    bonsai_require_file(bonsai_join_path(paths.tokenizer_path, "tokenizer.json"), "tokenizer");
    bonsai_require_file(
        bonsai_join_path(paths.tokenizer_path, "tokenizer_config.json"),
        "tokenizer config"
    );
}

void require_text_encoder_inventory(const BonsaiSafetensorsIndex& index) {
    const std::string embedding_key = index.resolve_model_prefixed_key("embed_tokens.weight");
    index.require_packed_weight(embedding_key, 4, 64);
}

std::string build_context_summary(const BonsaiRuntimeModelContext& context) {
    std::ostringstream output;
    output
        << "quantization_bits=" << context.quantization.bits
        << " group_size=" << context.quantization.group_size
        << " tokenizer_class=" << context.tokenizer_data.metadata.runtime_tokenizer_class
        << " tokenizer_vocab=" << context.tokenizer_data.metadata.vocab_size
        << " tokenizer_merges=" << context.tokenizer_data.metadata.merge_count
        << " tokenizer_pad_id=" << context.tokenizer_data.metadata.pad_token_id
        << " tokenizer_eos_id=" << context.tokenizer_data.metadata.eos_token_id
        << " tokenizer_checksum="
            << bonsai_tokenizer_data_checksum(context.tokenizer_data)
        << " transformer_tensors=" << context.transformer_index.tensor_count()
        << " transformer_files=" << context.transformer_index.file_count()
        << " transformer_logical_tensors="
            << context.transformer_inventory.logical_tensor_count
        << " flux_double_blocks=" << context.transformer_inventory.double_block_count
        << " flux_single_blocks=" << context.transformer_inventory.single_block_count
        << " flux_double_view_blocks=" << context.transformer_views.double_blocks.size()
        << " flux_single_view_blocks=" << context.transformer_views.single_blocks.size()
        << " flux_transformer_view_bytes="
            << bonsai_flux_transformer_byte_count(context.transformer_views)
        << " text_encoder_tensors=" << context.text_encoder_index.tensor_count()
        << " text_encoder_files=" << context.text_encoder_index.file_count()
        << " qwen_layers=" << context.text_encoder_inventory.layer_count
        << " qwen_logical_tensors=" << context.text_encoder_inventory.logical_tensor_count
        << " qwen_view_layers=" << context.text_encoder_views.layers.size()
        << " qwen_view_bytes=" << bonsai_qwen_text_encoder_byte_count(
            context.text_encoder_views
        )
        << " vae_tensors=" << context.vae_index.tensor_count()
        << " vae_files=" << context.vae_index.file_count()
        << " vae_up_blocks=" << context.vae_inventory.up_block_count
        << " vae_resnet_blocks=" << context.vae_inventory.resnet_block_count
        << " vae_attention_blocks=" << context.vae_inventory.attention_block_count
        << " vae_decode_bytes=" << bonsai_vae_decode_byte_count(context.vae_views);
    return output.str();
}

std::string build_text_encoder_context_summary(
    const BonsaiTextEncoderRuntimeContext& context
) {
    std::ostringstream output;
    output
        << "tokenizer_class=" << context.tokenizer_data.metadata.runtime_tokenizer_class
        << " tokenizer_vocab=" << context.tokenizer_data.metadata.vocab_size
        << " tokenizer_merges=" << context.tokenizer_data.metadata.merge_count
        << " tokenizer_pad_id=" << context.tokenizer_data.metadata.pad_token_id
        << " tokenizer_eos_id=" << context.tokenizer_data.metadata.eos_token_id
        << " tokenizer_checksum="
            << bonsai_tokenizer_data_checksum(context.tokenizer_data)
        << " text_encoder_tensors=" << context.text_encoder_index.tensor_count()
        << " text_encoder_files=" << context.text_encoder_index.file_count()
        << " qwen_layers=" << context.text_encoder_inventory.layer_count
        << " qwen_logical_tensors=" << context.text_encoder_inventory.logical_tensor_count
        << " qwen_view_layers=" << context.text_encoder_views.layers.size()
        << " qwen_view_bytes=" << bonsai_qwen_text_encoder_byte_count(
            context.text_encoder_views
        );
    return output.str();
}

std::string build_flux_transformer_context_summary(
    const BonsaiFluxTransformerRuntimeContext& context
) {
    std::ostringstream output;
    output
        << "quantization_bits=" << context.quantization.bits
        << " group_size=" << context.quantization.group_size
        << " transformer_tensors=" << context.transformer_index.tensor_count()
        << " transformer_files=" << context.transformer_index.file_count()
        << " transformer_logical_tensors="
            << context.transformer_inventory.logical_tensor_count
        << " flux_double_blocks=" << context.transformer_inventory.double_block_count
        << " flux_single_blocks=" << context.transformer_inventory.single_block_count
        << " flux_double_view_blocks=" << context.transformer_views.double_blocks.size()
        << " flux_single_view_blocks=" << context.transformer_views.single_blocks.size()
        << " flux_transformer_view_bytes="
            << bonsai_flux_transformer_byte_count(context.transformer_views);
    return output.str();
}

std::string build_vae_context_summary(const BonsaiVaeRuntimeContext& context) {
    std::ostringstream output;
    output
        << "vae_tensors=" << context.vae_index.tensor_count()
        << " vae_files=" << context.vae_index.file_count()
        << " vae_up_blocks=" << context.vae_inventory.up_block_count
        << " vae_resnet_blocks=" << context.vae_inventory.resnet_block_count
        << " vae_attention_blocks=" << context.vae_inventory.attention_block_count
        << " vae_decode_bytes=" << bonsai_vae_decode_byte_count(context.vae_views);
    return output.str();
}

} // namespace

BonsaiTextEncoderRuntimeContext::BonsaiTextEncoderRuntimeContext(
    const BonsaiModelPaths& model_paths
) :
    paths(model_paths),
    tokenizer_data(bonsai_load_tokenizer_data(model_paths.tokenizer_path)),
    text_encoder_index(BonsaiSafetensorsIndex::load_directory(
        model_paths.text_encoder_path,
        "text encoder"
    )),
    text_encoder_storage(text_encoder_index),
    text_encoder_views(bonsai_require_qwen_text_encoder_views(
        text_encoder_storage,
        text_encoder_index
    )) {
    require_text_encoder_inventory(text_encoder_index);
    text_encoder_inventory = bonsai_require_qwen_text_encoder_tensors(text_encoder_index);
    summary = build_text_encoder_context_summary(*this);
}

BonsaiFluxTransformerRuntimeContext::BonsaiFluxTransformerRuntimeContext(
    const BonsaiModelPaths& model_paths
) :
    paths(model_paths),
    quantization(bonsai_read_quantization_config(model_paths.packed_transformer_path)),
    transformer_index(BonsaiSafetensorsIndex::load_directory(
        model_paths.packed_transformer_path,
        "transformer"
    )),
    transformer_storage(transformer_index),
    transformer_views(bonsai_require_flux_transformer_views(
        transformer_storage,
        transformer_index,
        quantization.bits,
        quantization.group_size
    )) {
    transformer_inventory = bonsai_require_flux_transformer_tensors(
        transformer_index,
        quantization.bits,
        quantization.group_size
    );
    summary = build_flux_transformer_context_summary(*this);
}

BonsaiVaeRuntimeContext::BonsaiVaeRuntimeContext(const BonsaiModelPaths& model_paths) :
    paths(model_paths),
    vae_config(bonsai_read_vae_config(model_paths.vae_path)),
    vae_index(BonsaiSafetensorsIndex::load_directory(model_paths.vae_path, "vae")),
    vae_storage(vae_index),
    vae_views(bonsai_vae_require_decode_views(vae_storage, vae_index, vae_config)) {
    vae_inventory = bonsai_require_flux_vae_tensors(vae_index, vae_config);
    summary = build_vae_context_summary(*this);
}

BonsaiRuntimeModelContext::BonsaiRuntimeModelContext(const BonsaiModelPaths& model_paths) :
    paths(model_paths),
    quantization(bonsai_read_quantization_config(model_paths.packed_transformer_path)),
    vae_config(bonsai_read_vae_config(model_paths.vae_path)),
    tokenizer_data(bonsai_load_tokenizer_data(model_paths.tokenizer_path)),
    transformer_index(BonsaiSafetensorsIndex::load_directory(
        model_paths.packed_transformer_path,
        "transformer"
    )),
    text_encoder_index(BonsaiSafetensorsIndex::load_directory(
        model_paths.text_encoder_path,
        "text encoder"
    )),
    vae_index(BonsaiSafetensorsIndex::load_directory(model_paths.vae_path, "vae")),
    transformer_storage(transformer_index),
    text_encoder_storage(text_encoder_index),
    vae_storage(vae_index),
    transformer_views(bonsai_require_flux_transformer_views(
        transformer_storage,
        transformer_index,
        quantization.bits,
        quantization.group_size
    )),
    text_encoder_views(bonsai_require_qwen_text_encoder_views(
        text_encoder_storage,
        text_encoder_index
    )),
    vae_views(bonsai_vae_require_decode_views(vae_storage, vae_index, vae_config)) {
    transformer_inventory = bonsai_require_flux_transformer_tensors(
        transformer_index,
        quantization.bits,
        quantization.group_size
    );
    require_text_encoder_inventory(text_encoder_index);
    text_encoder_inventory = bonsai_require_qwen_text_encoder_tensors(text_encoder_index);
    vae_inventory = bonsai_require_flux_vae_tensors(vae_index, vae_config);
    summary = build_context_summary(*this);
}

std::unique_ptr<BonsaiRuntimeModelContext> bonsai_load_runtime_model_context(
    const BonsaiModelPaths& paths
) {
    require_runtime_layout(paths);
    return std::make_unique<BonsaiRuntimeModelContext>(paths);
}

std::unique_ptr<BonsaiTextEncoderRuntimeContext> bonsai_load_text_encoder_runtime_context(
    const BonsaiModelPaths& paths
) {
    require_runtime_layout(paths);
    return std::make_unique<BonsaiTextEncoderRuntimeContext>(paths);
}

std::unique_ptr<BonsaiFluxTransformerRuntimeContext> bonsai_load_flux_transformer_runtime_context(
    const BonsaiModelPaths& paths
) {
    require_runtime_layout(paths);
    return std::make_unique<BonsaiFluxTransformerRuntimeContext>(paths);
}

std::unique_ptr<BonsaiVaeRuntimeContext> bonsai_load_vae_runtime_context(
    const BonsaiModelPaths& paths
) {
    require_runtime_layout(paths);
    return std::make_unique<BonsaiVaeRuntimeContext>(paths);
}
