package com.shifthackz.aisdv1.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `OpenAiImageRaw` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class OpenAiImageRaw(
    /**
     * Exposes the `b64json` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("b64_json")
    val b64json: String? = null,
    /**
     * Exposes the `url` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("url")
    val url: String? = null,
    /**
     * Exposes the `revisedPrompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("revised_prompt")
    val revisedPrompt: String? = null,
)
