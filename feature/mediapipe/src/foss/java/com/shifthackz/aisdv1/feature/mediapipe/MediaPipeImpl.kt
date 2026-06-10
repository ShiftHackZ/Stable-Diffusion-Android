package com.shifthackz.aisdv1.feature.mediapipe

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe

internal class MediaPipeImpl : MediaPipe {

    override suspend fun process(payload: TextToImagePayload): Bitmap {
        throw IllegalStateException("Google AI MediaPipe is not supported on FOSS build.")
    }
}
