package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `GetGalleryItemsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetGalleryItemsUseCaseImpl(
    /**
     * Exposes the `generationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationResultRepository: GenerationResultRepository,
) : GetGalleryItemsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param ids ids value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(ids: List<Long>) = generationResultRepository.getByIds(ids)
}
