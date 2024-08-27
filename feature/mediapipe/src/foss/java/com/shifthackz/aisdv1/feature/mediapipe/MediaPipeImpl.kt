package com.shifthackz.aisdv1.feature.mediapipe

import android.graphics.Bitmap
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.mediapipe.MediaPipe
import io.reactivex.rxjava3.core.Single

internal class MediaPipeImpl : MediaPipe {

    override fun process(payload: TextToImagePayload): Single<Bitmap> {
        return Single.error(IllegalStateException("Google AI MediaPipe is not supported on FOSS build."))
    }
}
