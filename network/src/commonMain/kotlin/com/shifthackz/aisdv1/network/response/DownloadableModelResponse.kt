package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.Serializable

/**
 * Carries `DownloadableModelResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class DownloadableModelResponse(
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
     * Exposes the `size` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val size: String?,
    /**
     * Exposes the `sources` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val sources: List<String>?,
)
