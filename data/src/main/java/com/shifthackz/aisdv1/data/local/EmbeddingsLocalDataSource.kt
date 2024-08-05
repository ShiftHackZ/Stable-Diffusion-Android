package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity

internal class EmbeddingsLocalDataSource(
    private val dao: StableDiffusionEmbeddingDao,
) : EmbeddingsDataSource.Local {

    override fun getEmbeddings() = dao
        .queryAll()
        .map(List<StableDiffusionEmbeddingEntity>::mapEntityToDomain)

    override fun insertEmbeddings(list: List<Embedding>) = dao
        .deleteAll()
        .andThen(dao.insertList(list.mapDomainToEntity()))
}
