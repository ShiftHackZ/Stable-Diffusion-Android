package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

interface TextToImageUseCase {
    suspend operator fun invoke(payload: TextToImagePayload): List<AiGenerationResult>
}
