package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ReportRequest(
    val text: String,
    val reason: String,
    val image: String,
    @SerialName("server_source")
    val serverSource: String,
    val model: String,
)
