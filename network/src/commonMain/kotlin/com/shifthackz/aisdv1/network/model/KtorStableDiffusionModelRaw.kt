package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `KtorStableDiffusionModelRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class KtorStableDiffusionModelRaw(
    /**
     * Exposes the `title` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("title")
    val title: String? = null,
    /**
     * Exposes the `modelName` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("model_name")
    val modelName: String? = null,
    /**
     * Exposes the `hash` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("hash")
    val hash: String? = null,
    /**
     * Exposes the `sha256` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("sha256")
    val sha256: String? = null,
    /**
     * Exposes the `filename` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("filename")
    val filename: String? = null,
    /**
     * Exposes the `config` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("config")
    val config: String? = null,
)
