package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.Serializable

/**
 * Carries `SwarmUiModelRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class SwarmUiModelRaw(
    /**
     * Exposes the `name` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val name: String? = null,
    /**
     * Exposes the `title` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String? = null,
    /**
     * Exposes the `author` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val author: String? = null,
)
