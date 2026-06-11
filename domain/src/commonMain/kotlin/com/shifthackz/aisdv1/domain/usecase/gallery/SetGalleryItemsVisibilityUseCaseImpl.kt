package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `SetGalleryItemsVisibilityUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class SetGalleryItemsVisibilityUseCaseImpl(
    /**
     * Exposes the `generationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationResultRepository: GenerationResultRepository,
) : SetGalleryItemsVisibilityUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @param hidden hidden value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(ids: List<Long>, hidden: Boolean) {
        generationResultRepository.setVisibilityByIds(ids, hidden)
    }
}
