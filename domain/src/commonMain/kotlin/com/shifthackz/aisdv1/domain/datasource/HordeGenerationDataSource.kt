package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.HordeProcessStatus
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `HordeGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface HordeGenerationDataSource {
    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : HordeGenerationDataSource {
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
        /**
         * Executes the `imageToImage` step in the SDAI domain layer.
         *
         * @param apiKey api key value consumed by the API.
         * @param payload generation payload used by the operation.
         * @return Result produced by `imageToImage`.
         * @author Dmitriy Moroz
         */
        suspend fun imageToImage(apiKey: String, payload: ImageToImagePayload): AiGenerationResult
        /**
         * Performs the SDAI side effect handled by `interruptGeneration`.
         *
         * @param apiKey api key value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun interruptGeneration(apiKey: String)
    }

    /**
     * Defines the `StatusSource` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface StatusSource : HordeGenerationDataSource {
        /**
         * Exposes the `id` value used by the SDAI domain layer.
         *
         * @author Dmitriy Moroz
         */
        var id: String?
        /**
         * Loads SDAI data through `observe`.
         *
         * @return Result produced by `observe`.
         * @author Dmitriy Moroz
         */
        fun observe(): Flow<HordeProcessStatus>
        /**
         * Performs the SDAI side effect handled by `update`.
         *
         * @param status status value consumed by the API.
         * @author Dmitriy Moroz
         */
        fun update(status: HordeProcessStatus)
    }
}
