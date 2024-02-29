package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class HuggingFaceGenerationRequest(
    @SerializedName("inputs")
    val inputs: Any,
    @SerializedName("parameters")
    val parameters: Map<String, Any>,
)
