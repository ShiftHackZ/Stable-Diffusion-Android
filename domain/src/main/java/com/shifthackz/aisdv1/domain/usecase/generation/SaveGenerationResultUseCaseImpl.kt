package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class SaveGenerationResultUseCaseImpl(
    private val repository: GenerationResultRepository,
) : SaveGenerationResultUseCase {

    override fun invoke(result: AiGenerationResult) = repository
        .insert(result)
        .ignoreElement()
}
