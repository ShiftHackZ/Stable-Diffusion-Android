package com.shifthackz.aisdv1.domain.usecase.downloadable

/**
 * Defines the `DeleteModelUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DeleteModelUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(id: String)
}
