package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import io.reactivex.rxjava3.core.Single

fun interface TextToImageDemo {
    fun getDemoBase64(payload: TextToImagePayload): Single<AiGenerationResult>
}
