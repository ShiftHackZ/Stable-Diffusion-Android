package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

sealed interface HuggingFaceGenerationDataSource {
    interface Remote : HuggingFaceGenerationDataSource {
        suspend fun validateApiKey(apiKey: String): Boolean

        suspend fun textToImage(
            apiKey: String,
            modelName: String,
            payload: TextToImagePayload,
        ): AiGenerationResult

        suspend fun imageToImage(
            apiKey: String,
            modelName: String,
            payload: ImageToImagePayload,
        ): AiGenerationResult
    }
}
