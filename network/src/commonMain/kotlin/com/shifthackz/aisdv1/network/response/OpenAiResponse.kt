package com.shifthackz.aisdv1.network.response

import com.shifthackz.aisdv1.network.model.OpenAiImageRaw
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `OpenAiResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class OpenAiResponse(
    /**
     * Exposes the `created` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("created")
    val created: Long? = null,
    /**
     * Exposes the `data` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("data")
    val data: List<OpenAiImageRaw>? = null,
)
