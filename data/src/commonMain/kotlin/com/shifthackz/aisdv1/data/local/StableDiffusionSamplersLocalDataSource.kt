package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionSamplerDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionSamplerEntity

internal class StableDiffusionSamplersLocalDataSource(
    private val dao: StableDiffusionSamplerDao,
) : StableDiffusionSamplersDataSource.Local {

    override suspend fun getSamplers(): List<StableDiffusionSampler> = dao
        .queryAll()
        .let(List<StableDiffusionSamplerEntity>::mapEntityToDomain)

    override suspend fun insertSamplers(samplers: List<StableDiffusionSampler>) {
        dao.deleteAll()
        dao.insertList(samplers.mapDomainToEntity())
    }
}
