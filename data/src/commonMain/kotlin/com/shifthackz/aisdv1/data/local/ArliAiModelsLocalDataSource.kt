package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapArliAiEntityToDomain
import com.shifthackz.aisdv1.data.mappers.mapDomainToArliAiEntity
import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.dao.ArliAiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity

/**
 * Reads and writes cached ArliAI checkpoint metadata.
 *
 * @param dao Room DAO for the ArliAI model cache table.
 *
 * @author Dmitriy Moroz
 */
internal class ArliAiModelsLocalDataSource(
    private val dao: ArliAiModelDao,
) : ArliAiModelsDataSource.Local {

    /**
     * Reads all cached ArliAI checkpoints.
     *
     * @return locally stored ArliAI checkpoints mapped into domain models.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<StableDiffusionModel> = dao
        .queryAll()
        .let(List<ArliAiModelEntity>::mapArliAiEntityToDomain)

    /**
     * Replaces the cached ArliAI checkpoint list.
     *
     * @param models latest checkpoint metadata returned by the provider.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun insertModels(models: List<StableDiffusionModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToArliAiEntity())
    }
}
