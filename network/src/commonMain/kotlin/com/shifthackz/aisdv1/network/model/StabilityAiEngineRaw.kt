package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

@Serializable
data class StabilityAiEngineRaw(
    val description: String? = null,
    val id: String? = null,
    val name: String? = null,
    val type: String? = null,
)
