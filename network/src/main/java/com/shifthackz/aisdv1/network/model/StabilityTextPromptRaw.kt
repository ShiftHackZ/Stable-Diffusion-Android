package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class StabilityTextPromptRaw(
    @SerializedName("text")
    val text: String,
    @SerializedName("weight")
    val weight: Double,
)
