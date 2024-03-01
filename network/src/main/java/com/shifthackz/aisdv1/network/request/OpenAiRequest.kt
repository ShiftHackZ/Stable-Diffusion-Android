package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class OpenAiRequest(
    @SerializedName("prompt")
    val prompt: String,
    @SerializedName("model")
    val model: String,
    @SerializedName("size")
    val size: String,
    @SerializedName("response_format")
    val responseFormat: String = "b64_json",
    @SerializedName("quality")
    val quality: String?,
    @SerializedName("style")
    val style: String?,
)
