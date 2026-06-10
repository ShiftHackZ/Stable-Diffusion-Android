package com.shifthackz.aisdv1.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `OpenAiRequest` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class OpenAiRequest(
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String,
    /**
     * Exposes the `model` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("model")
    val model: String,
    /**
     * Exposes the `size` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("size")
    val size: String,
    /**
     * Exposes the `quality` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("quality")
    val quality: String? = null,
)
