package com.shifthackz.aisdv1.demo.serialize

import com.google.gson.annotations.SerializedName

internal data class DemoAsset(
    @SerializedName("images")
    val images: List<String>,
)
