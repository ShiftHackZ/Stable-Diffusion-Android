package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import io.reactivex.rxjava3.core.Single

interface FetchAndGetEmbeddingsUseCase {
    operator fun invoke(): Single<List<StableDiffusionEmbedding>>
}
