package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class TextToImageRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("negative_prompt")
    val negativePrompt: String,
    @SerializedName("steps")
    val steps: Int,
    @SerializedName("width")
    val width: Int,
    @SerializedName("height")
    val height: Int,
    @SerializedName("restore_faces")
    val restoreFaces: Boolean,
)
