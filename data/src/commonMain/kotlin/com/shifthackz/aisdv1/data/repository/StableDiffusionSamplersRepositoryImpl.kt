package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

internal class StableDiffusionSamplersRepositoryImpl(
    private val remoteDataSource: StableDiffusionSamplersDataSource.Remote,
    private val localDataSource: StableDiffusionSamplersDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : StableDiffusionSamplersRepository {

    override suspend fun fetchSamplers() {
        val samplers = remoteDataSource.fetchSamplers(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertSamplers(samplers)
    }

    override suspend fun getSamplers() = localDataSource.getSamplers()
}
