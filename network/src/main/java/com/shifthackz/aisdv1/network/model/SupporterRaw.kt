package com.shifthackz.aisdv1.network.model

import com.google.gson.annotations.SerializedName

data class SupporterRaw(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("date")
    val date: String?,
    @SerializedName("amount")
    val amount: String?,
    @SerializedName("currency")
    val currency: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("message")
    val message: String?,
)
