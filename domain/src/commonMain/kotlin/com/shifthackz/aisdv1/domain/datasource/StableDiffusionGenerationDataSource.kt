package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `StableDiffusionGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface StableDiffusionGenerationDataSource {
    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : StableDiffusionGenerationDataSource {
        /**
         * Executes the `checkAvailability` step in the SDAI domain layer.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun checkAvailability(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        )

        /**
         * Executes the `textToImage` step in the SDAI domain layer.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `textToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun textToImage(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            payload: TextToImagePayload,
        ): List<AiGenerationResult>

        /**
         * Executes the `imageToImage` step in the SDAI domain layer.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `imageToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun imageToImage(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            payload: ImageToImagePayload,
        ): List<AiGenerationResult>

        /**
         * Performs the SDAI side effect handled by `interruptGeneration`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun interruptGeneration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        )
    }
}
