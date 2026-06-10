package com.shifthackz.aisdv1.domain.demo

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ImageToImageDemo {
    suspend fun getDemoBase64(payload: ImageToImagePayload): AiGenerationResult
}
