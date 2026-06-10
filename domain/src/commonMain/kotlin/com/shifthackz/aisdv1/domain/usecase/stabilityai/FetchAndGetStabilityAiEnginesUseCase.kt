package com.shifthackz.aisdv1.domain.usecase.stabilityai

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

/**
 * Defines the `FetchAndGetStabilityAiEnginesUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchAndGetStabilityAiEnginesUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<StabilityAiEngine>
}
