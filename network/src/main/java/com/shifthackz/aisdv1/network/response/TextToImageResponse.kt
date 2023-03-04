package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class TextToImageResponse(
    @SerializedName("images")
    val images: List<String>,
)
