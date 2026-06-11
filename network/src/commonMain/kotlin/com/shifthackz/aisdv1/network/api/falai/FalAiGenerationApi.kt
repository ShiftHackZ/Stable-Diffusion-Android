package com.shifthackz.aisdv1.network.api.falai

import com.shifthackz.aisdv1.network.request.FalAiImageToImageRequest
import com.shifthackz.aisdv1.network.request.FalAiTextToImageRequest
import com.shifthackz.aisdv1.network.response.FalAiGenerationResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueStatusResponse
import com.shifthackz.aisdv1.network.response.FalAiQueueSubmitResponse

/**
 * Defines the `FalAiGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface FalAiGenerationApi {
    /**
     * Executes the `validateApiKey` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun validateApiKey(apiKey: String)

    /**
     * Executes the `submitTextToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @param request request value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun submitTextToImage(
        apiKey: String,
        model: String,
        request: FalAiTextToImageRequest,
    ): FalAiQueueSubmitResponse

    /**
     * Executes the `submitImageToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @param request request value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun submitImageToImage(
        apiKey: String,
        model: String,
        request: FalAiImageToImageRequest,
    ): FalAiQueueSubmitResponse

    /**
     * Executes the `getQueueStatus` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param statusUrl remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    suspend fun getQueueStatus(apiKey: String, statusUrl: String): FalAiQueueStatusResponse

    /**
     * Executes the `getQueueResult` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param responseUrl remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    suspend fun getQueueResult(apiKey: String, responseUrl: String): FalAiGenerationResponse

    /**
     * Executes the `downloadImage` step in the SDAI network layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    suspend fun downloadImage(url: String): ByteArray
}
