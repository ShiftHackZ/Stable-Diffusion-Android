package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository

/**
 * Implements `ServerConfigurationRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ServerConfigurationRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: ServerConfigurationDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: ServerConfigurationDataSource.Local,
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
) : ServerConfigurationRepository {

    /**
     * Loads SDAI data through `fetchConfiguration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchConfiguration() {
        val configuration = remoteDataSource.fetchConfiguration(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.save(configuration)
    }

    /**
     * Loads SDAI data through `fetchAndGetConfiguration`.
     *
     * @return Result produced by `fetchAndGetConfiguration`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetConfiguration(): ServerConfiguration {
        runCatching { fetchConfiguration() }
        return getConfiguration()
    }

    /**
     * Loads SDAI data through `getConfiguration`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getConfiguration() = localDataSource.get()

    /**
     * Performs the SDAI side effect handled by `updateConfiguration`.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun updateConfiguration(configuration: ServerConfiguration) {
        remoteDataSource.updateConfiguration(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
            configuration = configuration,
        )
    }
}
