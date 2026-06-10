package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao

/**
 * Coordinates `HuggingFaceModelsLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class HuggingFaceModelsLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: HuggingFaceModelDao,
) : HuggingFaceModelsDataSource.Local {

    /**
     * Loads SDAI data through `getAll`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getAll() = dao
        .query()
        .mapEntityToDomain()

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param models models value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun save(models: List<HuggingFaceModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToEntity())
    }
}
