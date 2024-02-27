package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionModelsLocalDataSource(
    private val dao: StableDiffusionModelDao,
) : StableDiffusionModelsDataSource.Local {

    override fun insertModels(models: List<StableDiffusionModel>): Completable = dao
        .deleteAll()
        .andThen(dao.insertList(models.mapDomainToEntity()))

    override fun getModels(): Single<List<StableDiffusionModel>> = dao
        .queryAll()
        .map(List<StableDiffusionModelEntity>::mapEntityToDomain)
}
