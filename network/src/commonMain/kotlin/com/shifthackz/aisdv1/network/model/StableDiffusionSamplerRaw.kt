package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StableDiffusionSamplerRaw(
    @SerialName("name")
    val name: String? = null,
    @SerialName("aliases")
    val aliases: List<String>? = null,
    @SerialName("options")
    val options: Map<String, String>? = null,
)
