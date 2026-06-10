package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity

/**
 * Coordinates `StableDiffusionModelsLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionModelsLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: StableDiffusionModelDao,
) : StableDiffusionModelsDataSource.Local {

    /**
     * Loads SDAI data through `getModels`.
     *
     * @return Result produced by `getModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<StableDiffusionModel> = dao
        .queryAll()
        .let(List<StableDiffusionModelEntity>::mapEntityToDomain)

    /**
     * Performs the SDAI side effect handled by `insertModels`.
     *
     * @param models models value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertModels(models: List<StableDiffusionModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToEntity())
    }
}
