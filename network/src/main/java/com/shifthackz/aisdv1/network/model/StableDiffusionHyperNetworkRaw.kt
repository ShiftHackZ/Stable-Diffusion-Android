package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StableDiffusionHyperNetworkRaw(
    @SerializedName("name")
    val name: String?,
    @SerializedName("path")
    val path: String?,
)
