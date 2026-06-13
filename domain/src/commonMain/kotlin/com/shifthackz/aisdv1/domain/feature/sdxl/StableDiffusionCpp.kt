package com.shifthackz.aisdv1.domain.feature.sdxl

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

interface StableDiffusionCpp {
    suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String

    suspend fun interrupt()

    fun observeStatus(): Flow<LocalDiffusionStatus>
}
