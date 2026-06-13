package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `ArliAiGenerationRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ArliAiGenerationRepository {
    suspend fun validateApiKey(): Boolean

    suspend fun generateFromText(payload: TextToImagePayload): List<AiGenerationResult>

    suspend fun generateFromImage(payload: ImageToImagePayload): List<AiGenerationResult>
}
