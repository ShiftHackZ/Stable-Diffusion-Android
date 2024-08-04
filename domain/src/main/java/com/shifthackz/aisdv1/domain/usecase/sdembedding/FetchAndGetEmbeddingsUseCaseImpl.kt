package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetEmbeddingsUseCaseImpl(
    private val repository: StableDiffusionEmbeddingsRepository,
) : FetchAndGetEmbeddingsUseCase {

    override fun invoke(): Single<List<StableDiffusionEmbedding>> = repository.fetchAndGetEmbeddings()
}
