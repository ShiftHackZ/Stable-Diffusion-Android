package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ImageToImageRequest(
    @SerialName("init_images")
    val initImages: List<String>,
    @SerialName("include_init_images")
    val includeInitImages: Boolean,
    @SerialName("mask")
    val mask: String?,
    @SerialName("inpainting_mask_invert")
    val inPaintingMaskInvert: Int?,
    @SerialName("inpaint_full_res_padding")
    val inPaintFullResPadding: Int?,
    @SerialName("inpainting_fill")
    val inPaintingFill: Int?,
    @SerialName("inpaint_full_res")
    val inPaintFullRes: Boolean?,
    @SerialName("mask_blur")
    val maskBlur: Int?,
    @SerialName("denoising_strength")
    val denoisingStrength: Float,
    @SerialName("prompt")
    val prompt: String,
    @SerialName("negative_prompt")
    val negativePrompt: String,
    @SerialName("steps")
    val steps: Int,
    @SerialName("cfg_scale")
    val cfgScale: Float,
    @SerialName("width")
    val width: Int,
    @SerialName("height")
    val height: Int,
    @SerialName("batch_size")
    val batchSize: Int,
    @SerialName("restore_faces")
    val restoreFaces: Boolean,
    @SerialName("seed")
    val seed: String?,
    @SerialName("subseed")
    val subSeed: String?,
    @SerialName("subseed_strength")
    val subSeedStrength: Float?,
    @SerialName("sampler_index")
    val samplerIndex: String,
)
