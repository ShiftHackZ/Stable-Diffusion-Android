package com.shifthackz.aisdv1.domain.usecase.stabilityai

import kotlinx.coroutines.flow.Flow

/**
 * Defines the `ObserveStabilityAiCreditsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ObserveStabilityAiCreditsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Flow<Float>
}
