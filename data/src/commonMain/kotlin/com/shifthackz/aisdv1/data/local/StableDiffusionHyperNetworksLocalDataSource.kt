package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao

internal class StableDiffusionHyperNetworksLocalDataSource(
    private val dao: StableDiffusionHyperNetworkDao,
) : StableDiffusionHyperNetworksDataSource.Local {

    override suspend fun getHyperNetworks() = dao
        .queryAll()
        .mapEntityToDomain()

    override suspend fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>) {
        dao.deleteAll()
        dao.insertList(list.mapDomainToEntity())
    }
}
