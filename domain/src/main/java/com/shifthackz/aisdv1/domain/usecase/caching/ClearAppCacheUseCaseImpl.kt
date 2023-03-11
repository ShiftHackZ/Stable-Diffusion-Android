package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

internal class ClearAppCacheUseCaseImpl(
    private val repository: GenerationResultRepository,
) : ClearAppCacheUseCase {

    override fun invoke() = repository.deleteAll()
}
