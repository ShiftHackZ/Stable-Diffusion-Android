package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `HuggingFaceModelRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class HuggingFaceModelRaw(
    /**
     * Exposes the `id` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val id: String?,
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String?,
    /**
     * Exposes the `alias` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val alias: String?,
    /**
     * Exposes the `source` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val source: String?,
)
