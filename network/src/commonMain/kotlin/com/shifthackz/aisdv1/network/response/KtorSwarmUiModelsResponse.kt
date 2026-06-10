package com.shifthackz.aisdv1.network.response

import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import kotlinx.serialization.Serializable

@Serializable
data class KtorSwarmUiModelsResponse(
    val files: List<SwarmUiModelRaw>? = null,
)
