package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload

interface ImageToImageUseCase {
    suspend operator fun invoke(payload: ImageToImagePayload): List<AiGenerationResult>
}
