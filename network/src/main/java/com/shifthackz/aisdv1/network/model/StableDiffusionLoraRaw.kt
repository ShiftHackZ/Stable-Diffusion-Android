package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StableDiffusionLoraRaw(
    @SerializedName("name")
    val name: String?,
    @SerializedName("alias")
    val alias: String?,
    @SerializedName("path")
    val path: String?,
)
