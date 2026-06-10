package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class SwarmUiModelRaw(
    val name: String? = null,
    val title: String? = null,
    val author: String? = null,
)
