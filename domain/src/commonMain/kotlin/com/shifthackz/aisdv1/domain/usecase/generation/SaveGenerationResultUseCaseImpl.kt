package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class SaveGenerationResultUseCaseImpl(
    private val repository: GenerationResultRepository,
) : SaveGenerationResultUseCase {

    override suspend fun invoke(result: AiGenerationResult): Long =
        result.id.takeIf { it > 0L } ?: repository.insert(result)
}
