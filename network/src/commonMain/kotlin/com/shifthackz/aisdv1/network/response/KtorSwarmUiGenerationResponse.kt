package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable

/**
 * Carries `KtorSwarmUiGenerationResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class KtorSwarmUiGenerationResponse(
    /**
     * Exposes the `images` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val images: List<String>? = null,
)
