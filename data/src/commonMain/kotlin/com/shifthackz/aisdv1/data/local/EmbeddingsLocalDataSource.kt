package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionEmbeddingDao

/**
 * Coordinates `EmbeddingsLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class EmbeddingsLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: StableDiffusionEmbeddingDao,
) : EmbeddingsDataSource.Local {

    /**
     * Loads SDAI data through `getEmbeddings`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getEmbeddings() = dao
        .queryAll()
        .mapEntityToDomain()

    /**
     * Performs the SDAI side effect handled by `insertEmbeddings`.
     *
     * @param list list value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertEmbeddings(list: List<Embedding>) {
        dao.deleteAll()
        dao.insertList(list.mapDomainToEntity())
    }
}
