package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.storage.db.cache.dao.SwarmUiModelDao
import com.shifthackz.aisdv1.storage.db.cache.entity.SwarmUiModelEntity

internal class SwarmUiModelsLocalDataSource(
    private val dao: SwarmUiModelDao,
) : SwarmUiModelsDataSource.Local {

    override suspend fun getModels(): List<SwarmUiModel> = dao
        .queryAll()
        .let(List<SwarmUiModelEntity>::mapEntityToDomain)

    override suspend fun insertModels(models: List<SwarmUiModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToEntity())
    }
}
