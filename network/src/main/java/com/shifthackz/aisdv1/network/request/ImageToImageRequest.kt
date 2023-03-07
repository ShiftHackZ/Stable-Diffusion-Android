package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class ImageToImageRequest(
    @SerializedName("init_images")
    val initImages: List<String>,
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
    @SerializedName("sampler_index")
    val samplerIndex: String,
)
