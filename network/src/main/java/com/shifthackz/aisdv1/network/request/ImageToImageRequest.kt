package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class ImageToImageRequest(
    @SerializedName("init_images")
    val initImages: List<String>,
    @SerializedName("include_init_images")
    val includeInitImages: Boolean,
    @SerializedName("mask")
    val mask: String?,
    @SerializedName("inpainting_mask_invert")
    val inPaintingMaskInvert: Int?,
    @SerializedName("inpaint_full_res_padding")
    val inPaintFullResPadding: Int?,
    @SerializedName("inpainting_fill")
    val inPaintingFill: Int?,
    @SerializedName("inpaint_full_res")
    val inPaintFullRes: Boolean?,
    @SerializedName("mask_blur")
    val maskBlur: Int?,
    @SerializedName("denoising_strength")
    val denoisingStrength: Float,
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("negative_prompt")
    val negativePrompt: String,
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("cfg_scale")
    val cfgScale: Float,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("restore_faces")
    val restoreFaces: Boolean,
    @SerializedName("seed")
    val seed: String?,
    @SerializedName("subseed")
    val subSeed: String?,
    @SerializedName("subseed_strength")
    val subSeedStrength: Float?,
    @SerializedName("sampler_index")
    val samplerIndex: String,
)
