package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `OpenAiGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface OpenAiGenerationDataSource {
    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : OpenAiGenerationDataSource {
        /**
         * Executes the `validateApiKey` step in the SDAI domain layer.
         *
         * @param apiKey api key value consumed by the API.
         * @return Result produced by `validateApiKey`.
         * @author Dmitriy Moroz
         */
        suspend fun validateApiKey(apiKey: String): Boolean
        /**
         * Executes the `textToImage` step in the SDAI domain layer.
         *
         * @param apiKey api key value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `textToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun textToImage(apiKey: String, payload: TextToImagePayload): AiGenerationResult
    }
}
