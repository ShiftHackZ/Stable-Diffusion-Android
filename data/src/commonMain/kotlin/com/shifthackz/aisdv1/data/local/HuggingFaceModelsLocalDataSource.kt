package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao

internal class HuggingFaceModelsLocalDataSource(
    private val dao: HuggingFaceModelDao,
) : HuggingFaceModelsDataSource.Local {

    override suspend fun getAll() = dao
        .query()
        .mapEntityToDomain()

    override suspend fun save(models: List<HuggingFaceModel>) {
        dao.deleteAll()
        dao.insertList(models.mapDomainToEntity())
    }
}
