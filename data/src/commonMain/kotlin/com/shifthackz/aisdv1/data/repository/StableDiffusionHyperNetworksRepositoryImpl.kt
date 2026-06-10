package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

/**
 * Implements `StableDiffusionHyperNetworksRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionHyperNetworksRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StableDiffusionHyperNetworksDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: StableDiffusionHyperNetworksDataSource.Local,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
    /**
     * Exposes the `authorizationStore` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val authorizationStore: AuthorizationStore,
) : StableDiffusionHyperNetworksRepository {

    /**
     * Loads SDAI data through `fetchHyperNetworks`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchHyperNetworks() {
        if (preferenceManager.source != ServerSource.AUTOMATIC1111) return
        val hyperNetworks = remoteDataSource.fetchHyperNetworks(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertHyperNetworks(hyperNetworks)
    }

    /**
     * Loads SDAI data through `fetchAndGetHyperNetworks`.
     *
     * @return Result produced by `fetchAndGetHyperNetworks`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetHyperNetworks(): List<StableDiffusionHyperNetwork> {
        runCatching { fetchHyperNetworks() }
        return getHyperNetworks()
    }

    /**
     * Loads SDAI data through `getHyperNetworks`.
     *
     * @return Result produced by `getHyperNetworks`.
     * @author Dmitriy Moroz
     */
    override suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork> =
        localDataSource.getHyperNetworks()
}
