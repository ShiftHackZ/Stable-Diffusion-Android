package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository
import io.reactivex.rxjava3.core.Single

internal class GetGenerationResultUseCaseImpl(
    private val repository: GenerationResultRepository,
) : GetGenerationResultUseCase {

    override operator fun invoke(id: Long): Single<AiGenerationResult> = repository.getById(id)
}
