package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class SwarmUiGenerationResponse(
    @SerializedName("images")
    val images: List<String>?,
)
