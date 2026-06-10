package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

/**
 * Carries `KtorSdEmbeddingsResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class KtorSdEmbeddingsResponse(
    /**
     * Exposes the `loaded` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val loaded: Map<String, JsonElement>? = null,
    /**
     * Exposes the `loadedKeysOverride` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @Transient
    private val loadedKeysOverride: Set<String>? = null,
) {
    val loadedKeys: Set<String>
        get() = loadedKeysOverride ?: loaded?.keys.orEmpty()

    companion object {
        fun fromLoadedKeys(keys: Set<String>) = KtorSdEmbeddingsResponse(
            loadedKeysOverride = keys,
        )
    }
}
