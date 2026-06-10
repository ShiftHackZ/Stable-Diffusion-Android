package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface SaveGenerationResultUseCase {
    suspend operator fun invoke(result: AiGenerationResult): Long
}
