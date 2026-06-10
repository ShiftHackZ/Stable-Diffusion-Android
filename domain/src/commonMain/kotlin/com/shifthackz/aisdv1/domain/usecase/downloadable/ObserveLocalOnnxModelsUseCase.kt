package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `ObserveLocalOnnxModelsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ObserveLocalOnnxModelsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Flow<List<LocalAiModel>>
}
