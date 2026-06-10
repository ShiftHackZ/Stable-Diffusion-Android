package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

fun interface TextToImageDemo {
    suspend fun getDemoBase64(payload: TextToImagePayload): AiGenerationResult
}
