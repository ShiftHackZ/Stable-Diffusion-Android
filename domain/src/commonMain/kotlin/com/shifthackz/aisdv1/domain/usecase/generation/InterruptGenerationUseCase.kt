package com.shifthackz.aisdv1.domain.usecase.generation

/**
 * Defines the `InterruptGenerationUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface InterruptGenerationUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}
