package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class SdEmbeddingsResponse(
    @SerializedName("loaded")
    val loaded: Map<String, Any?>?,
)
