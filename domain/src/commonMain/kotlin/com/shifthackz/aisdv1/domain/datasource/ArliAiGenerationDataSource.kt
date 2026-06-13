package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `ArliAiGenerationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ArliAiGenerationDataSource {
    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ArliAiGenerationDataSource {
        suspend fun validateApiKey(apiKey: String): Boolean

        suspend fun textToImage(
            apiKey: String,
            model: String,
            payload: TextToImagePayload,
        ): List<AiGenerationResult>

        suspend fun imageToImage(
            apiKey: String,
            model: String,
            payload: ImageToImagePayload,
        ): List<AiGenerationResult>
    }
}
