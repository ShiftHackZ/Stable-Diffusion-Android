package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapToEntity
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSamplerDomain
import com.shifthackz.aisdv1.storage.database.dao.StableDiffusionSamplerDao
import io.reactivex.rxjava3.core.Completable

class StableDiffusionSamplersLocalDataSource(
    private val dao: StableDiffusionSamplerDao,
) : StableDiffusionSamplersDataSource.Local {

    override fun insertSamplers(samplers: List<StableDiffusionSamplerDomain>): Completable = dao
        .insertList(samplers.mapToEntity())
}
