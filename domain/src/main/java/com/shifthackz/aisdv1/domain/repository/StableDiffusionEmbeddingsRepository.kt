package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionEmbeddingsRepository {
    fun fetchEmbeddings(): Completable
    fun fetchAndGetEmbeddings(): Single<List<StableDiffusionEmbedding>>
    fun getEmbeddings(): Single<List<StableDiffusionEmbedding>>
}
