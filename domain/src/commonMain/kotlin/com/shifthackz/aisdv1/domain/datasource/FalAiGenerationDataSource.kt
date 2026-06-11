package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `FalAiGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface FalAiGenerationDataSource {
    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : FalAiGenerationDataSource {
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
        suspend fun textToImage(apiKey: String, payload: TextToImagePayload): List<AiGenerationResult>

        /**
         * Executes the `imageToImage` step in the SDAI domain layer.
         *
         * @param apiKey api key value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `imageToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun imageToImage(apiKey: String, payload: ImageToImagePayload): List<AiGenerationResult>
    }
}
