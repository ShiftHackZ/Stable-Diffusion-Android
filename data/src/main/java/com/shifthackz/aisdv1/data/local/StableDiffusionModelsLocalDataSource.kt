package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import com.shifthackz.aisdv1.storage.database.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.database.entity.StableDiffusionModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionModelsLocalDataSource(
    private val dao: StableDiffusionModelDao,
) : StableDiffusionModelsDataSource.Local {

    override fun insertModels(models: List<StableDiffusionModelDomain>): Completable = dao
        .insertList(models.mapDomainToEntity())

    override fun getModels(): Single<List<StableDiffusionModelDomain>> = dao
        .queryAll()
        .map(List<StableDiffusionModelEntity>::mapEntityToDomain)
}
