package com.shifthackz.aisdv1.domain.feature.mediapipe

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

interface MediaPipe {
    fun process(payload: TextToImagePayload): Single<Bitmap>
}
