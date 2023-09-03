package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StableDiffusionSamplerRaw(
    @SerializedName("name")
    val name: String?,
    @SerializedName("aliases")
    val aliases: List<String>?,
    @SerializedName("options")
    val options: Map<String, String>?,
)
