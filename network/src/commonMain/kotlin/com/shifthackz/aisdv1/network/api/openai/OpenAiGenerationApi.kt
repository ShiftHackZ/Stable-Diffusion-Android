package com.shifthackz.aisdv1.network.api.openai

import com.shifthackz.aisdv1.network.request.OpenAiRequest
import com.shifthackz.aisdv1.network.response.OpenAiResponse

/**
 * Defines the `OpenAiGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface OpenAiGenerationApi {

    /**
     * Executes the `validateBearerToken` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun validateBearerToken(apiKey: String)

    /**
     * Executes the `generateImage` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `generateImage`.
     * @author Dmitriy Moroz
     */
    suspend fun generateImage(apiKey: String, request: OpenAiRequest): OpenAiResponse
}
