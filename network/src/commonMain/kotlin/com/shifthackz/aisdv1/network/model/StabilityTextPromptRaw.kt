package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StabilityTextPromptRaw(
    @SerialName("text")
    val text: String,
    @SerialName("weight")
    val weight: Double,
)
