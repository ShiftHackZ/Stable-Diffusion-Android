package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `SaveLastResultToCacheUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SaveLastResultToCacheUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(result: AiGenerationResult): AiGenerationResult
}
