package com.shifthackz.aisdv1.network.request

import com.google.gson.annotations.SerializedName

data class ReportRequest(
    @SerializedName("text")
    val text: String,
    @SerializedName("reason")
    val reason: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("server_source")
    val serverSource: String,
    @SerializedName("model")
    val model: String,
)
