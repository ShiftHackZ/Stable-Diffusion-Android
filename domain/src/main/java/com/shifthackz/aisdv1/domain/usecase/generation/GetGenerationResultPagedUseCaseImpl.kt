package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single

internal class GetGenerationResultPagedUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGenerationResultPagedUseCase {

    override operator fun invoke(limit: Int, offset: Int): Single<List<AiGenerationResult>> = repository.getPage(limit, offset)
}
