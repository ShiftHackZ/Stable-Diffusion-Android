package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class DownloadableModelResponse(
    @SerializedName("id")
    val id: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("size")
    val size: String?,
    @SerializedName("sources")
    val sources: List<String>?,
)
