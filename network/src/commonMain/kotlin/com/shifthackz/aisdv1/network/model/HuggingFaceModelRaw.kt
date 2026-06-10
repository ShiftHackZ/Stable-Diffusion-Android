package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class HuggingFaceModelRaw(
    val id: String?,
    val name: String?,
    val alias: String?,
    val source: String?,
)
