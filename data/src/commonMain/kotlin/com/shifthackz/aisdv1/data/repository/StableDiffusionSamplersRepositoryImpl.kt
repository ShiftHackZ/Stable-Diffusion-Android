package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

/**
 * Implements `StableDiffusionSamplersRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionSamplersRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StableDiffusionSamplersDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: StableDiffusionSamplersDataSource.Local,
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
) : StableDiffusionSamplersRepository {

    /**
     * Loads SDAI data through `fetchSamplers`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSamplers() {
        val samplers = remoteDataSource.fetchSamplers(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertSamplers(samplers)
    }

    /**
     * Loads SDAI data through `getSamplers`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getSamplers() = localDataSource.getSamplers()
}
