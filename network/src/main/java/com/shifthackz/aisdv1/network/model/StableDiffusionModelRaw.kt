package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StableDiffusionModelRaw(
    @SerializedName("title")
    val title: String,
    @SerializedName("model_name")
    val modelName: String,
    @SerializedName("hash")
    val hash: String?,
    @SerializedName("sha256")
    val sha256: String?,
    @SerializedName("filename")
    val filename: String,
    @SerializedName("config")
    val config: String?,
)
