package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface OpenAiGenerationRepository {
    suspend fun validateApiKey(): Boolean
    suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult
}
