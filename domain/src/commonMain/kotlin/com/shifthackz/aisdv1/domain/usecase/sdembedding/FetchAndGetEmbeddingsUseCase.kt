package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.Embedding

interface FetchAndGetEmbeddingsUseCase {
    suspend operator fun invoke(): List<Embedding>
}
