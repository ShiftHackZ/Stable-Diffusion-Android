package com.shifthackz.aisdv1.domain.usecase.gallery

/**
 * Defines the `SetGalleryItemsLikedUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SetGalleryItemsLikedUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @param liked liked value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(ids: List<Long>, liked: Boolean)
}
