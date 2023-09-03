package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class DownloadableModelResponse(
    @SerializedName("name")
    val name: String?,
    @SerializedName("sources")
    val sources: List<String>?,
)
