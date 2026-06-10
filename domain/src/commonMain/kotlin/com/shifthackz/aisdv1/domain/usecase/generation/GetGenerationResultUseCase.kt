package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface GetGenerationResultUseCase {
    suspend operator fun invoke(id: Long): AiGenerationResult
}
