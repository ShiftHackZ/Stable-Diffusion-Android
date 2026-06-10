package com.shifthackz.aisdv1.domain.usecase.gallery

/**
 * Defines the `DeleteGalleryItemsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DeleteGalleryItemsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(ids: List<Long>)
}
