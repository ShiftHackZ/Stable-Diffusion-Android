package com.shifthackz.aisdv1.domain.usecase.sdscript

/**
 * Defines the `IsADetailerAvailableUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface IsADetailerAvailableUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): Boolean
}
