package com.shifthackz.aisdv1.domain.usecase.connectivity

/**
 * Defines the `TestFalAiApiKeyUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface TestFalAiApiKeyUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): Boolean
}
