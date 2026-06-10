package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao

/**
 * Coordinates `LorasLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class LorasLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: StableDiffusionLoraDao,
) : LorasDataSource.Local {

    /**
     * Loads SDAI data through `getLoras`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getLoras() = dao
        .queryAll()
        .mapEntityToDomain()

    /**
     * Performs the SDAI side effect handled by `insertLoras`.
     *
     * @param loras loras value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertLoras(loras: List<LoRA>) {
        dao.deleteAll()
        dao.insertList(loras.mapDomainToEntity())
    }
}
