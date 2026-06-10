package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao

internal class LorasLocalDataSource(
    private val dao: StableDiffusionLoraDao,
) : LorasDataSource.Local {

    override suspend fun getLoras() = dao
        .queryAll()
        .mapEntityToDomain()

    override suspend fun insertLoras(loras: List<LoRA>) {
        dao.deleteAll()
        dao.insertList(loras.mapDomainToEntity())
    }
}
