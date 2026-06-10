package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository

internal class StableDiffusionModelsRepositoryImpl(
    private val remoteDataSource: StableDiffusionModelsDataSource.Remote,
    private val localDataSource: StableDiffusionModelsDataSource.Local,
    private val preferenceManager: PreferenceManager,
    private val authorizationStore: AuthorizationStore,
) : StableDiffusionModelsRepository {

    override suspend fun fetchModels() {
        val models = remoteDataSource.fetchSdModels(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertModels(models)
    }

    override suspend fun fetchAndGetModels(): List<StableDiffusionModel> {
        runCatching { fetchModels() }
        return getModels()
    }

    override suspend fun getModels(): List<StableDiffusionModel> =
        localDataSource.getModels()
}
