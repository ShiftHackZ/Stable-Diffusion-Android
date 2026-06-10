package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface StableDiffusionGenerationRepository {
    suspend fun checkApiAvailability()
    suspend fun checkApiAvailability(url: String)
    suspend fun generateFromText(payload: TextToImagePayload): List<AiGenerationResult>
    suspend fun generateFromImage(payload: ImageToImagePayload): List<AiGenerationResult>
    suspend fun interruptGeneration()
}
