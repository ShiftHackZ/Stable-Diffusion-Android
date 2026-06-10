package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository

/**
 * Implements `StableDiffusionModelsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class StableDiffusionModelsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StableDiffusionModelsDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: StableDiffusionModelsDataSource.Local,
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
) : StableDiffusionModelsRepository {

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels() {
        val models = remoteDataSource.fetchSdModels(
            baseUrl = preferenceManager.automatic1111ServerUrl,
            credentials = authorizationStore.getAuthorizationCredentials(),
        )
        localDataSource.insertModels(models)
    }

    /**
     * Loads SDAI data through `fetchAndGetModels`.
     *
     * @return Result produced by `fetchAndGetModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetModels(): List<StableDiffusionModel> {
        runCatching { fetchModels() }
        return getModels()
    }

    /**
     * Loads SDAI data through `getModels`.
     *
     * @return Result produced by `getModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<StableDiffusionModel> =
        localDataSource.getModels()
}
