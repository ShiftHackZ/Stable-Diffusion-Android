package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface SdaiCloudGenerationRepository {
    suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult
    suspend fun generateFromImage(payload: ImageToImagePayload): AiGenerationResult
}

object NoOpSdaiCloudGenerationRepository : SdaiCloudGenerationRepository {
    override suspend fun generateFromText(payload: TextToImagePayload): AiGenerationResult =
        error("SDAI Cloud generation is unavailable in this build.")

    override suspend fun generateFromImage(payload: ImageToImagePayload): AiGenerationResult =
        error("SDAI Cloud generation is unavailable in this build.")
}
