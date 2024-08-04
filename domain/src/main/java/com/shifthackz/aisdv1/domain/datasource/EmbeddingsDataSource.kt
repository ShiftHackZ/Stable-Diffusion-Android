package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Embedding
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface EmbeddingsDataSource {

    interface Remote : EmbeddingsDataSource {

        interface Automatic1111 : Remote {
            fun fetchEmbeddings(): Single<List<Embedding>>
        }

        interface SwarmUi : Remote {
            fun fetchEmbeddings(sessionId: String): Single<List<Embedding>>
        }
    }

    interface Local : EmbeddingsDataSource {
        fun getEmbeddings(): Single<List<Embedding>>
        fun insertEmbeddings(list: List<Embedding>): Completable
    }
}
