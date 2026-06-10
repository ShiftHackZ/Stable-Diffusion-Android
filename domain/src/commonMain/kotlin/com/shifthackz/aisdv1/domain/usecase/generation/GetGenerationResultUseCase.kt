package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `GetGenerationResultUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetGenerationResultUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(id: Long): AiGenerationResult
}
