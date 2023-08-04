package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.TemporaryGenerationResultRepository

internal class GetLastResultFromCacheUseCaseImpl(
    private val temporaryGenerationResultRepository: TemporaryGenerationResultRepository,
) : GetLastResultFromCacheUseCase {

    override fun invoke() = temporaryGenerationResultRepository.get()
}
