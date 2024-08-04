package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity

internal class LorasLocalDataSource(
    private val dao: StableDiffusionLoraDao,
) : LorasDataSource.Local {

    override fun getLoras() = dao
        .queryAll()
        .map(List<StableDiffusionLoraEntity>::mapEntityToDomain)

    override fun insertLoras(loras: List<LoRA>) = dao
        .deleteAll()
        .andThen(dao.insertList(loras.mapDomainToEntity()))
}
