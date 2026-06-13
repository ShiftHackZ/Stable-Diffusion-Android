package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapArliAiEntityToDomain
import com.shifthackz.aisdv1.data.mappers.mapDomainToArliAiEntity
import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.dao.ArliAiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ArliAiModelEntity

/**
 * Coordinates `ArliAiModelsLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ArliAiModelsLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: ArliAiModelDao,
) : ArliAiModelsDataSource.Local {

    /**
     * Loads SDAI data through `getModels`.
     *
     * @return Result produced by `getModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<StableDiffusionModel> = dao
        .queryAll()
        .let(List<ArliAiModelEntity>::mapArliAiEntityToDomain)

    /**
     * Performs the SDAI side effect handled by `insertModels`.
     *
     * @param models models value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertModels(models: List<StableDiffusionModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToArliAiEntity())
    }
}
