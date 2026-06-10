package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.storage.db.cache.dao.StableDiffusionHyperNetworkDao

/**
 * Coordinates `StableDiffusionHyperNetworksLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionHyperNetworksLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: StableDiffusionHyperNetworkDao,
) : StableDiffusionHyperNetworksDataSource.Local {

    /**
     * Loads SDAI data through `getHyperNetworks`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getHyperNetworks() = dao
        .queryAll()
        .mapEntityToDomain()

    /**
     * Performs the SDAI side effect handled by `insertHyperNetworks`.
     *
     * @param list list value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun insertHyperNetworks(list: List<StableDiffusionHyperNetwork>) {
        dao.deleteAll()
        dao.insertList(list.mapDomainToEntity())
    }
}
