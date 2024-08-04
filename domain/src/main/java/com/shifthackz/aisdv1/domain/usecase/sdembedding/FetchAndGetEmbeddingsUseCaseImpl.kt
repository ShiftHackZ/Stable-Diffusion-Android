package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetEmbeddingsUseCaseImpl(
    private val repository: EmbeddingsRepository,
) : FetchAndGetEmbeddingsUseCase {

    override fun invoke(): Single<List<Embedding>> = repository.fetchAndGetEmbeddings()
}
