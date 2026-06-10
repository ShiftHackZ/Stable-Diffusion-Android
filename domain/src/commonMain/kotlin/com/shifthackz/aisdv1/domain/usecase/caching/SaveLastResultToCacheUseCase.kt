package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface SaveLastResultToCacheUseCase {
    suspend operator fun invoke(result: AiGenerationResult): AiGenerationResult
}
