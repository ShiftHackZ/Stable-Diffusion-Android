package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Defines the `GetLocalMediaPipeModelsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetLocalMediaPipeModelsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<LocalAiModel>
}
