package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Embedding

interface EmbeddingsRepository {
    suspend fun fetchEmbeddings()
    suspend fun fetchAndGetEmbeddings(): List<Embedding>
    suspend fun getEmbeddings(): List<Embedding>
}
