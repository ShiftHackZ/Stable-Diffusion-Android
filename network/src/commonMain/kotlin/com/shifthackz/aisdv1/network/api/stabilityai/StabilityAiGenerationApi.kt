package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.request.StabilityTextToImageRequest
import com.shifthackz.aisdv1.network.response.StabilityCreditsResponse
import com.shifthackz.aisdv1.network.response.StabilityGenerationResponse

/**
 * Defines the `StabilityAiGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiGenerationApi {

    /**
     * Executes the `validateBearerToken` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun validateBearerToken(apiKey: String)

    /**
     * Loads SDAI data through `fetchCredits`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetchCredits`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchCredits(apiKey: String): StabilityCreditsResponse

    /**
     * Executes the `textToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param engineId engine id value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `textToImage`.
     * @author Dmitriy Moroz
     */
    suspend fun textToImage(
        apiKey: String,
        engineId: String,
        request: StabilityTextToImageRequest,
    ): StabilityGenerationResponse

    /**
     * Executes the `imageToImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param engineId engine id value consumed by the API.
     * @param imageBytes image bytes value consumed by the API.
     * @param parameters parameters value consumed by the API.
     * @return Result produced by `imageToImage`.
     * @author Dmitriy Moroz
     */
    suspend fun imageToImage(
        apiKey: String,
        engineId: String,
        imageBytes: ByteArray,
        parameters: Map<String, String>,
    ): StabilityGenerationResponse
}
