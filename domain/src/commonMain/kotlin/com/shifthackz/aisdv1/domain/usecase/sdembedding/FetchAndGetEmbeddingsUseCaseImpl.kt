package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository

class FetchAndGetEmbeddingsUseCaseImpl(
    private val repository: EmbeddingsRepository,
) : FetchAndGetEmbeddingsUseCase {

    override suspend fun invoke(): List<Embedding> = repository.fetchAndGetEmbeddings()
}
