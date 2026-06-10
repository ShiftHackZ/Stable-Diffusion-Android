package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object NoOpLocalDiffusionGenerationRepository : LocalDiffusionGenerationRepository {
    override fun observeStatus(): Flow<LocalDiffusionStatus> =
        flowOf(LocalDiffusionStatus(current = 0, total = 0))

    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("Local Diffusion generation is available on Android only.")

    override suspend fun interruptGeneration() = Unit
}

object NoOpMediaPipeGenerationRepository : MediaPipeGenerationRepository {
    override suspend fun generateFromText(payload: TextToImagePayload) =
        error("MediaPipe generation is available on Android only.")
}
