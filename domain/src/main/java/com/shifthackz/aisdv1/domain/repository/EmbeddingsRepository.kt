package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Embedding
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface EmbeddingsRepository {
    fun fetchEmbeddings(): Completable
    fun fetchAndGetEmbeddings(): Single<List<Embedding>>
    fun getEmbeddings(): Single<List<Embedding>>
}
