package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

sealed interface OpenAiGenerationDataSource {
    interface Remote : OpenAiGenerationDataSource {
        suspend fun validateApiKey(apiKey: String): Boolean
        suspend fun textToImage(apiKey: String, payload: TextToImagePayload): AiGenerationResult
    }
}
