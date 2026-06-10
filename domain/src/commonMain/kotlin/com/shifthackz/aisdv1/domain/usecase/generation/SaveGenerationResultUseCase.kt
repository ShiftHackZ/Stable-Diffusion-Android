package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `SaveGenerationResultUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SaveGenerationResultUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(result: AiGenerationResult): Long
}
