package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository
import io.reactivex.rxjava3.core.Single

internal class GetLastResultFromCacheUseCaseImpl(
    private val temporaryGenerationResultRepository: TemporaryGenerationResultRepository,
) : GetLastResultFromCacheUseCase {

    override fun invoke(): Single<AiGenerationResult> = temporaryGenerationResultRepository.get()
}
