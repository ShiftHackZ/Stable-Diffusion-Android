package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `SetGalleryItemsLikedUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class SetGalleryItemsLikedUseCaseImpl(
    /**
     * Exposes the `generationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationResultRepository: GenerationResultRepository,
) : SetGalleryItemsLikedUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @param liked liked value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(ids: List<Long>, liked: Boolean) {
        generationResultRepository.setLikedByIds(ids, liked)
    }
}
