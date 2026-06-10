package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
data class KtorSdEmbeddingsResponse(
    val loaded: Map<String, JsonElement>? = null,
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
