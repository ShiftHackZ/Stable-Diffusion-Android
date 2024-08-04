package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class SwarmUiModelRaw(
    @SerializedName("name")
    val name: String?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("author")
    val author: String?,
)
