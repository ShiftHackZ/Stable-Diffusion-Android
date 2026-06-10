package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

sealed interface StabilityAiGenerationDataSource {

    interface Remote : StabilityAiGenerationDataSource {

        suspend fun validateApiKey(apiKey: String): Boolean

        suspend fun textToImage(
            apiKey: String,
            engineId: String,
            payload: TextToImagePayload,
        ): AiGenerationResult

        suspend fun imageToImage(
            apiKey: String,
            engineId: String,
            payload: ImageToImagePayload,
        ): AiGenerationResult
    }
}
