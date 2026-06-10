package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao

internal class EmbeddingsLocalDataSource(
    private val dao: StableDiffusionEmbeddingDao,
) : EmbeddingsDataSource.Local {

    override suspend fun getEmbeddings() = dao
        .queryAll()
        .mapEntityToDomain()

    override suspend fun insertEmbeddings(list: List<Embedding>) {
        dao.deleteAll()
        dao.insertList(list.mapDomainToEntity())
    }
}
