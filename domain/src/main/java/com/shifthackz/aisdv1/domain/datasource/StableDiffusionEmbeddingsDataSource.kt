package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionEmbeddingsDataSource {

    interface Remote : StableDiffusionEmbeddingsDataSource {
        fun fetchEmbeddings(): Single<List<StableDiffusionEmbedding>>
    }

    interface Local : StableDiffusionEmbeddingsDataSource {
        fun getEmbeddings(): Single<List<StableDiffusionEmbedding>>
        fun insertEmbeddings(list: List<StableDiffusionEmbedding>): Completable
    }
}
