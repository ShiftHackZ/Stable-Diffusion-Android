package com.shifthackz.aisdv1.domain.usecase.caching

interface DataPreLoaderUseCase {
    suspend operator fun invoke()
}

object NoOpDataPreLoaderUseCase : DataPreLoaderUseCase {
    override suspend fun invoke() = Unit
}
