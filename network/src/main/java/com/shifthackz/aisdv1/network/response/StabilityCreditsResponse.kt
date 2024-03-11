package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class StabilityCreditsResponse(
    @SerializedName("credits")
    val credits: Float?,
)
