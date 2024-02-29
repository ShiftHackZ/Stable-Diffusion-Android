package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class HuggingFaceModelRaw(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("alias")
    val alias: String?,
    @SerializedName("source")
    val source: String?,
)
