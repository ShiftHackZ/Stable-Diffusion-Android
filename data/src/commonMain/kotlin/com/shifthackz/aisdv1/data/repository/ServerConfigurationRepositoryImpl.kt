package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository

internal class ServerConfigurationRepositoryImpl(
    private val remoteDataSource: ServerConfigurationDataSource.Remote,
    private val localDataSource: ServerConfigurationDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : ServerConfigurationRepository {

    override suspend fun fetchConfiguration() {
        val configuration = remoteDataSource.fetchConfiguration(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.save(configuration)
    }

    override suspend fun fetchAndGetConfiguration(): ServerConfiguration {
        runCatching { fetchConfiguration() }
        return getConfiguration()
    }

    override suspend fun getConfiguration() = localDataSource.get()

    override suspend fun updateConfiguration(configuration: ServerConfiguration) {
        remoteDataSource.updateConfiguration(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
            configuration = configuration,
        )
    }
}
