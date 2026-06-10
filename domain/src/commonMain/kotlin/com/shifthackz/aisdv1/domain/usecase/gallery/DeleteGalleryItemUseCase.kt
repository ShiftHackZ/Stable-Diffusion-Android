package com.shifthackz.aisdv1.domain.usecase.gallery

/**
 * Defines the `DeleteGalleryItemUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DeleteGalleryItemUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(id: Long)
}
