package com.shifthackz.aisdv1.network.response

import com.shifthackz.aisdv1.network.model.SwarmUiModelRaw
import kotlinx.serialization.Serializable

/**
 * Carries `KtorSwarmUiModelsResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class KtorSwarmUiModelsResponse(
    /**
     * Exposes the `files` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val files: List<SwarmUiModelRaw>? = null,
)
