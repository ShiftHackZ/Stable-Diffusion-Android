package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

object NoOpTextToImageDemo : TextToImageDemo {
    override suspend fun getDemoBase64(payload: TextToImagePayload) =
        error("Demo mode is not available on this platform.")
}

object NoOpImageToImageDemo : ImageToImageDemo {
    override suspend fun getDemoBase64(payload: ImageToImagePayload) =
        error("Demo mode is not available on this platform.")
}
