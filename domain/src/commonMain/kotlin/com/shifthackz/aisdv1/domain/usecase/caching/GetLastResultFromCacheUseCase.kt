package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `GetLastResultFromCacheUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetLastResultFromCacheUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): AiGenerationResult
}
