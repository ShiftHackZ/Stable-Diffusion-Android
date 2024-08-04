package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionEmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionEmbeddingEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionEmbeddingsLocalDataSource(
    private val dao: StableDiffusionEmbeddingDao,
) : StableDiffusionEmbeddingsDataSource.Local {

    override fun getEmbeddings(): Single<List<StableDiffusionEmbedding>> = dao
        .queryAll()
        .map(List<StableDiffusionEmbeddingEntity>::mapEntityToDomain)

    override fun insertEmbeddings(list: List<StableDiffusionEmbedding>): Completable = dao
        .deleteAll()
        .andThen(dao.insertList(list.mapDomainToEntity()))
}
