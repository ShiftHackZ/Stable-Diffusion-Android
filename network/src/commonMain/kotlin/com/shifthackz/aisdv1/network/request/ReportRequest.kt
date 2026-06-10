package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `ReportRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class ReportRequest(
    /**
     * Exposes the `text` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val text: String,
    /**
     * Exposes the `reason` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val reason: String,
    /**
     * Exposes the `image` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val image: String,
    /**
     * Exposes the `serverSource` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("server_source")
    val serverSource: String,
    /**
     * Exposes the `model` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    val model: String,
)
