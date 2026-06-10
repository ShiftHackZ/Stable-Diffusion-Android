package com.shifthackz.aisdv1.domain.usecase.downloadable

import com.shifthackz.aisdv1.domain.entity.LocalAiModel

/**
 * Defines the `GetLocalModelUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetLocalModelUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(id: String): LocalAiModel
}
