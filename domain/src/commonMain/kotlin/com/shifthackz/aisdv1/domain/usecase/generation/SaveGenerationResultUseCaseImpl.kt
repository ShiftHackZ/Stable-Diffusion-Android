package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `SaveGenerationResultUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class SaveGenerationResultUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : SaveGenerationResultUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param result result value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(result: AiGenerationResult): Long =
        result.id.takeIf { it > 0L } ?: repository.insert(result)
}
