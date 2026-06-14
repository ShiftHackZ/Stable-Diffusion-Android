package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `GetAllGalleryUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetAllGalleryUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : GetAllGalleryUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke() = repository.getAll()

    /**
     * Executes the `ids` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun ids() = repository.getAllIds()
}
