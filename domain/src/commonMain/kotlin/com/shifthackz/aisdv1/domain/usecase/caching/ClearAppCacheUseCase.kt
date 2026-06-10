package com.shifthackz.aisdv1.domain.usecase.caching

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

interface ClearAppCacheUseCase {
    suspend operator fun invoke()
}

internal class ClearAppCacheUseCaseImpl(
    private val appCacheCleaner: AppCacheCleaner,
    private val repository: GenerationResultRepository,
) : ClearAppCacheUseCase {

    override suspend fun invoke() {
        repository.deleteAll()
        appCacheCleaner.clear()
    }
}

interface AppCacheCleaner {
    suspend fun clear()
}

object NoOpAppCacheCleaner : AppCacheCleaner {
    override suspend fun clear() = Unit
}
