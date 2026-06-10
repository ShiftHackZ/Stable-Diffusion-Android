package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

/**
 * Coordinates `SwarmUiModelsLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class SwarmUiModelsLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: SwarmUiModelDao,
) : SwarmUiModelsDataSource.Local {

    /**
     * Loads SDAI data through `getModels`.
     *
     * @return Result produced by `getModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<SwarmUiModel> = dao
        .queryAll()
        .let(List<SwarmUiModelEntity>::mapEntityToDomain)

    /**
     * Performs the SDAI side effect handled by `insertModels`.
     *
     * @param models models value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertModels(models: List<SwarmUiModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToEntity())
    }
}
