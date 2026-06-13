package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `ArliAiTextToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ArliAiTextToImageRequest(
    @SerialName("sd_model_checkpoint")
    val sdModelCheckpoint: String,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negative_prompt")
    val negativePrompt: String,
    @SerialName("steps")
    val steps: Int,
    @SerialName("sampler_name")
    val samplerName: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("seed")
    val seed: Long?,
    @SerialName("cfg_scale")
    val cfgScale: Float,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    @SerialName("detailer_enabled")
    val detailerEnabled: Boolean? = null,
    @SerialName("detailer_prompt")
    val detailerPrompt: String? = null,
    @SerialName("detailer_negative")
    val detailerNegative: String? = null,
    @SerialName("detailer_steps")
    val detailerSteps: Int? = null,
    @SerialName("detailer_strength")
    val detailerStrength: Float? = null,
    @SerialName("detailer_model")
    val detailerModel: String? = null,
    @SerialName("detailer_conf")
    val detailerConfidence: Float? = null,
    @SerialName("detailer_padding")
    val detailerPadding: Int? = null,
    @SerialName("detailer_blur")
    val detailerBlur: Int? = null,
)

/**
 * Carries `ArliAiImageToImageRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ArliAiImageToImageRequest(
    @SerialName("sd_model_checkpoint")
    val sdModelCheckpoint: String,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negative_prompt")
    val negativePrompt: String,
    @SerialName("init_images")
    val initImages: List<String>,
    @SerialName("mask")
    val mask: String?,
    @SerialName("denoising_strength")
    val denoisingStrength: Float,
    @SerialName("steps")
    val steps: Int,
    @SerialName("sampler_name")
    val samplerName: String,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("seed")
    val seed: Long?,
    @SerialName("cfg_scale")
    val cfgScale: Float,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    @SerialName("mask_blur")
    val maskBlur: Int?,
    @SerialName("inpainting_fill")
    val inPaintingFill: Int?,
    @SerialName("inpaint_full_res")
    val inPaintFullRes: Boolean?,
    @SerialName("inpaint_full_res_padding")
    val inPaintFullResPadding: Int?,
    @SerialName("inpainting_mask_invert")
    val inPaintingMaskInvert: Int?,
    @SerialName("detailer_enabled")
    val detailerEnabled: Boolean? = null,
    @SerialName("detailer_prompt")
    val detailerPrompt: String? = null,
    @SerialName("detailer_negative")
    val detailerNegative: String? = null,
    @SerialName("detailer_steps")
    val detailerSteps: Int? = null,
    @SerialName("detailer_strength")
    val detailerStrength: Float? = null,
    @SerialName("detailer_model")
    val detailerModel: String? = null,
    @SerialName("detailer_conf")
    val detailerConfidence: Float? = null,
    @SerialName("detailer_padding")
    val detailerPadding: Int? = null,
    @SerialName("detailer_blur")
    val detailerBlur: Int? = null,
)
