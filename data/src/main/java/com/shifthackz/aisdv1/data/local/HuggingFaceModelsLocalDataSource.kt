package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.storage.db.persistent.dao.HuggingFaceModelDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.HuggingFaceModelEntity

internal class HuggingFaceModelsLocalDataSource(
    private val dao: HuggingFaceModelDao,
) : HuggingFaceModelsDataSource.Local {

    override fun getAll() = dao
        .query()
        .map(List<HuggingFaceModelEntity>::mapEntityToDomain)

    override fun save(models: List<HuggingFaceModel>) = dao
        .deleteAll()
        .andThen(dao.insertList(models.mapDomainToEntity()))
}
