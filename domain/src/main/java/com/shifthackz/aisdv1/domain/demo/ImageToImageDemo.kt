package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import io.reactivex.rxjava3.core.Single

fun interface ImageToImageDemo {
    fun getDemoBase64(payload: ImageToImagePayload): Single<AiGenerationResult>
}
