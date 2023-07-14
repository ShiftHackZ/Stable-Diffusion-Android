package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGenerationResultUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGenerationResultUseCase {

    override operator fun invoke(id: Long) = repository.getById(id)
}
