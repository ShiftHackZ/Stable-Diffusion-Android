package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface EmbeddingsDataSource {

    sealed interface Remote : EmbeddingsDataSource {

        interface Automatic1111 : Remote {
            suspend fun fetchEmbeddings(
                baseUrl: String,
                credentials: AuthorizationCredentials,
            ): List<Embedding>
        }

        interface SwarmUi : Remote {
            suspend fun fetchEmbeddings(
                baseUrl: String,
                sessionId: String,
                credentials: AuthorizationCredentials,
            ): List<Embedding>
        }
    }

    interface Local : EmbeddingsDataSource {
        suspend fun getEmbeddings(): List<Embedding>
        suspend fun insertEmbeddings(list: List<Embedding>)
    }
}
