package com.shifthackz.aisdv1.network.response

import com.google.gson.annotations.SerializedName

data class MotdResponse(
    @SerializedName("display")
    val display: Boolean?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("subtitle")
    val subTitle: String?,
)
