package com.shifthackz.aisdv1.domain.usecase.gallery

/**
 * Defines the `ToggleImageVisibilityUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ToggleImageVisibilityUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(id: Long): Boolean
}
