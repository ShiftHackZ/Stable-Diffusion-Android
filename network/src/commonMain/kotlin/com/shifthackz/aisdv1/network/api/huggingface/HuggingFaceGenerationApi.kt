package com.shifthackz.aisdv1.network.api.huggingface

import com.shifthackz.aisdv1.network.request.HuggingFaceGenerationRequest

/**
 * Defines the `HuggingFaceGenerationApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface HuggingFaceGenerationApi {

    /**
     * Executes the `validateBearerToken` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun validateBearerToken(apiKey: String)

    /**
     * Executes the `generate` step in the SDAI network layer.
     *
     * @param apiKey api key value consumed by the API.
     * @param model model value consumed by the API.
     * @param request request value consumed by the API.
     * @return Result produced by `generate`.
     * @author Dmitriy Moroz
     */
    suspend fun generate(
        apiKey: String,
        model: String,
        request: HuggingFaceGenerationRequest,
    ): ByteArray
}
