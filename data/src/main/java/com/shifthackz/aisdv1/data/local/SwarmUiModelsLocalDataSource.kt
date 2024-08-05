package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class SwarmUiModelsLocalDataSource(
    private val dao: SwarmUiModelDao,
) : SwarmUiModelsDataSource.Local {

    override fun getModels(): Single<List<SwarmUiModel>> = dao
        .queryAll()
        .map(List<SwarmUiModelEntity>::mapEntityToDomain)

    override fun insertModels(models: List<SwarmUiModel>): Completable = dao
        .deleteAll()
        .andThen(dao.insertList(models.mapDomainToEntity()))
}
