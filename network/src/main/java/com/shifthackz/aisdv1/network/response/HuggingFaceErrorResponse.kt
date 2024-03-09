package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class HuggingFaceErrorResponse(
    @SerializedName("error")
    val error: String?,
    @SerializedName("estimated_time")
    val estimatedTime: Double?,
)
