package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `DeleteAllGalleryUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class DeleteAllGalleryUseCaseImpl(
    /**
     * Exposes the `generationResultRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val generationResultRepository: GenerationResultRepository,
) : DeleteAllGalleryUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() {
        generationResultRepository.deleteAll()
    }
}
