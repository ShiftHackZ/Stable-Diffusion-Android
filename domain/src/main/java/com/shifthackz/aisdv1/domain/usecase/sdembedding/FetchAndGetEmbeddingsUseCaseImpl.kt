package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository

internal class FetchAndGetEmbeddingsUseCaseImpl(
    private val repository: StableDiffusionEmbeddingsRepository,
) : FetchAndGetEmbeddingsUseCase {

    override fun invoke() = repository.fetchAndGetEmbeddings()
}
