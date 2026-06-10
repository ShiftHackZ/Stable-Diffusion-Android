package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `GetGenerationResultUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetGenerationResultUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : GetGenerationResultUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param id identifier of the target entity.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(id: Long) = repository.getById(id)
}
