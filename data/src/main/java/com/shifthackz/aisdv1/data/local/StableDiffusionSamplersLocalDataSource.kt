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

    override fun getSamplers() = dao
        .queryAll()
        .map(List<StableDiffusionSamplerEntity>::mapEntityToDomain)

    override fun insertSamplers(samplers: List<StableDiffusionSampler>) = dao
        .deleteAll()
        .andThen(dao.insertList(samplers.mapDomainToEntity()))
}
