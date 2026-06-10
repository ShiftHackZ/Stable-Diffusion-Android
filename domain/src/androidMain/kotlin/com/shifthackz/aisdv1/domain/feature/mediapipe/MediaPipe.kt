package com.shifthackz.aisdv1.domain.feature.mediapipe

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface MediaPipe {
    suspend fun process(payload: TextToImagePayload): Bitmap
}
