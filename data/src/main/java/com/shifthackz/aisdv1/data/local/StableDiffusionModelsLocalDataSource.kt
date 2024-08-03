package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity

internal class StableDiffusionModelsLocalDataSource(
    private val dao: StableDiffusionModelDao,
) : StableDiffusionModelsDataSource.Local {

    override fun getModels() = dao
        .queryAll()
        .map(List<StableDiffusionModelEntity>::mapEntityToDomain)

    override fun insertModels(models: List<StableDiffusionModel>) = dao
        .deleteAll()
        .andThen(dao.insertList(models.mapDomainToEntity()))
}
