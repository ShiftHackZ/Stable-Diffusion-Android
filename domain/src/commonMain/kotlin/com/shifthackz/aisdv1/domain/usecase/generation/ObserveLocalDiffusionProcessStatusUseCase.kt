package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `ObserveLocalDiffusionProcessStatusUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ObserveLocalDiffusionProcessStatusUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Flow<LocalDiffusionStatus>
}
