package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionHyperNetworksDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionHyperNetwork
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionHyperNetworksRepository

internal class StableDiffusionHyperNetworksRepositoryImpl(
    private val remoteDataSource: StableDiffusionHyperNetworksDataSource.Remote,
    private val localDataSource: StableDiffusionHyperNetworksDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : StableDiffusionHyperNetworksRepository {

    override suspend fun fetchHyperNetworks() {
        if (preferenceManager.source != ServerSource.AUTOMATIC1111) return
        val hyperNetworks = remoteDataSource.fetchHyperNetworks(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertHyperNetworks(hyperNetworks)
    }

    override suspend fun fetchAndGetHyperNetworks(): List<StableDiffusionHyperNetwork> {
        runCatching { fetchHyperNetworks() }
        return getHyperNetworks()
    }

    override suspend fun getHyperNetworks(): List<StableDiffusionHyperNetwork> =
        localDataSource.getHyperNetworks()
}
