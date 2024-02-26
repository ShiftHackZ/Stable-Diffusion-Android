package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao
import com.shifthackz.aisdv1.storage.db.cache.entity.StableDiffusionHyperNetworkEntity

internal class StableDiffusionHyperNetworksLocalDataSource(
    private val dao: StableDiffusionHyperNetworkDao,
) : StableDiffusionHyperNetworksDataSource.Local {

    override fun getHyperNetworks() = dao
        .queryAll()
        .map(List<StableDiffusionHyperNetworkEntity>::mapEntityToDomain)

    override fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>) = list
        .mapDomainToEntity()
        .let(dao::insertList)
}
