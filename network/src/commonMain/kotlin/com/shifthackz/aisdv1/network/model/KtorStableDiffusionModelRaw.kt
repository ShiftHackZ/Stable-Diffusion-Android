package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class KtorStableDiffusionModelRaw(
    @SerialName("title")
    val title: String? = null,
    @SerialName("model_name")
    val modelName: String? = null,
    @SerialName("hash")
    val hash: String? = null,
    @SerialName("sha256")
    val sha256: String? = null,
    @SerialName("filename")
    val filename: String? = null,
    @SerialName("config")
    val config: String? = null,
)
