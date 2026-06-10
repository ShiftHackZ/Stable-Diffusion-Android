package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult

/**
 * Defines the `GetGalleryItemsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetGalleryItemsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(ids: List<Long>): List<AiGenerationResult>
}
