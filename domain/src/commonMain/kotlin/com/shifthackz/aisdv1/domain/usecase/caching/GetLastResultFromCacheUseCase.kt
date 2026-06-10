package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

interface GetLastResultFromCacheUseCase {
    suspend operator fun invoke(): AiGenerationResult
}
