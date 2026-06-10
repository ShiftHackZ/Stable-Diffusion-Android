package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable

@Serializable
data class KtorSwarmUiGenerationResponse(
    val images: List<String>? = null,
)
