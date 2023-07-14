package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class GetGenerationResultPagedUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGenerationResultPagedUseCase {

    override operator fun invoke(limit: Int, offset: Int) = repository.getPage(limit, offset)
}
