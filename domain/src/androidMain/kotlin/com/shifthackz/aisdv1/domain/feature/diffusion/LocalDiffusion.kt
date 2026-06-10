package com.shifthackz.aisdv1.domain.feature.diffusion

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import kotlinx.coroutines.flow.Flow

interface LocalDiffusion {
    suspend fun process(payload: TextToImagePayload): Bitmap
    suspend fun interrupt()
    fun observeStatus(): Flow<LocalDiffusionStatus>
}
