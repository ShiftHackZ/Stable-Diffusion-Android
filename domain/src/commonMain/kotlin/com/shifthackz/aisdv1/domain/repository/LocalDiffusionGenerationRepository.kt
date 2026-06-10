package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

interface LocalDiffusionGenerationRepository {
    fun observeStatus(): Flow<LocalDiffusionStatus>
    suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult
    suspend fun interruptGeneration()
}
