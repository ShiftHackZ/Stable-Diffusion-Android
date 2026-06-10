package com.shifthackz.aisdv1.domain.usecase.gallery

/**
 * Defines the `DeleteAllGalleryUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DeleteAllGalleryUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}
