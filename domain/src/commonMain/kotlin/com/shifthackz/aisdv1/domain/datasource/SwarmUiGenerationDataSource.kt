package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `SwarmUiGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface SwarmUiGenerationDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : SwarmUiGenerationDataSource {
        /**
         * Executes the `textToImage` step in the SDAI domain layer.
         *
         * @param baseUrl base url value consumed by the API.
         * @param sessionId session id value consumed by the API.
         * @param model model value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `textToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun textToImage(
            baseUrl: String,
            sessionId: String,
            model: String,
            credentials: AuthorizationCredentials,
            payload: TextToImagePayload,
        ): AiGenerationResult

        /**
         * Executes the `imageToImage` step in the SDAI domain layer.
         *
         * @param baseUrl base url value consumed by the API.
         * @param sessionId session id value consumed by the API.
         * @param model model value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `imageToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun imageToImage(
            baseUrl: String,
            sessionId: String,
            model: String,
            credentials: AuthorizationCredentials,
            payload: ImageToImagePayload,
        ): AiGenerationResult
    }
}
