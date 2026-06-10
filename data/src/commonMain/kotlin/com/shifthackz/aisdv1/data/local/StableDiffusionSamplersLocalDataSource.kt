package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

/**
 * Coordinates `StableDiffusionSamplersLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionSamplersLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: StableDiffusionSamplerDao,
) : StableDiffusionSamplersDataSource.Local {

    /**
     * Loads SDAI data through `getSamplers`.
     *
     * @return Result produced by `getSamplers`.
     * @author Dmitriy Moroz
     */
    override suspend fun getSamplers(): List<StableDiffusionSampler> = dao
        .queryAll()
        .let(List<StableDiffusionSamplerEntity>::mapEntityToDomain)

    /**
     * Performs the SDAI side effect handled by `insertSamplers`.
     *
     * @param samplers samplers value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertSamplers(samplers: List<StableDiffusionSampler>) {
        dao.deleteAll()
        dao.insertList(samplers.mapDomainToEntity())
    }
}
