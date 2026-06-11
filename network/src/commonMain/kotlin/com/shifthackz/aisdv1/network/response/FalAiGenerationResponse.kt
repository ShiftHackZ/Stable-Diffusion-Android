package com.shifthackz.aisdv1.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Carries `FalAiQueueSubmitResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiQueueSubmitResponse(
    /**
     * Exposes the `requestId` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("request_id")
    val requestId: String? = null,
    /**
     * Exposes the `status` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("status")
    val status: String? = null,
    /**
     * Exposes the `responseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("response_url")
    val responseUrl: String? = null,
    /**
     * Exposes the `statusUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("status_url")
    val statusUrl: String? = null,
    /**
     * Exposes the `cancelUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("cancel_url")
    val cancelUrl: String? = null,
)

/**
 * Carries `FalAiQueueStatusResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiQueueStatusResponse(
    /**
     * Exposes the `status` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("status")
    val status: String? = null,
    /**
     * Exposes the `requestId` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("request_id")
    val requestId: String? = null,
    /**
     * Exposes the `responseUrl` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("response_url")
    val responseUrl: String? = null,
    /**
     * Exposes the `queuePosition` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("queue_position")
    val queuePosition: Int? = null,
    /**
     * Exposes the `error` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("error")
    val error: String? = null,
)

/**
 * Carries `FalAiGenerationResponse` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiGenerationResponse(
    /**
     * Exposes the `images` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("images")
    val images: List<FalAiImage>? = null,
    /**
     * Exposes the `seed` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("seed")
    val seed: Long? = null,
    /**
     * Exposes the `prompt` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("prompt")
    val prompt: String? = null,
    /**
     * Exposes the `hasNsfwConcepts` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("has_nsfw_concepts")
    val hasNsfwConcepts: List<Boolean>? = null,
)

/**
 * Carries `FalAiImage` data through the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
@Serializable
data class FalAiImage(
    /**
     * Exposes the `url` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("url")
    val url: String? = null,
    /**
     * Exposes the `width` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("width")
    val width: Int? = null,
    /**
     * Exposes the `height` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("height")
    val height: Int? = null,
    /**
     * Exposes the `contentType` value used by the SDAI network layer.
     *
     * @author Dmitriy Moroz
     */
    @SerialName("content_type")
    val contentType: String? = null,
)
