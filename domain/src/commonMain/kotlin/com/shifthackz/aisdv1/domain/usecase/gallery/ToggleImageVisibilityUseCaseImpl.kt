package com.shifthackz.aisdv1.domain.usecase.gallery

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `ToggleImageVisibilityUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ToggleImageVisibilityUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : ToggleImageVisibilityUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(id: Long) = repository.toggleVisibility(id)
}
