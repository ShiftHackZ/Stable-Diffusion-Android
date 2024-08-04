package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionLorasDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionLoraDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionLoraEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionLorasLocalDataSource(
    private val dao: StableDiffusionLoraDao,
) : StableDiffusionLorasDataSource.Local {

    override fun getLoras(): Single<List<StableDiffusionLora>> = dao
        .queryAll()
        .map(List<StableDiffusionLoraEntity>::mapEntityToDomain)

    override fun insertLoras(loras: List<StableDiffusionLora>): Completable = dao
        .deleteAll()
        .andThen(dao.insertList(loras.mapDomainToEntity()))
}
