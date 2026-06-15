package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ArliAiModelsRepository

/**
 * Synchronizes ArliAI checkpoint metadata between the provider and local cache.
 *
 * @param remoteDataSource ArliAI model-list source backed by the network API.
 * @param localDataSource ArliAI model-list source backed by the cache database.
 * @param preferenceManager supplies the saved ArliAI API key.
 *
 * @author Dmitriy Moroz
 */
internal class ArliAiModelsRepositoryImpl(
    private val remoteDataSource: ArliAiModelsDataSource.Remote,
    private val localDataSource: ArliAiModelsDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : ArliAiModelsRepository {

    /**
     * Refreshes provider checkpoints and replaces the local cache.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels() {
        val models = remoteDataSource.fetchModels(preferenceManager.arliAiApiKey)
        localDataSource.insertModels(models)
    }

    /**
     * Attempts a refresh and always returns the cached checkpoint list.
     *
     * Network errors are ignored here so UI can continue using stale cached models.
     *
     * @return cached ArliAI checkpoints after the refresh attempt.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetModels(): List<StableDiffusionModel> {
        runCatching { fetchModels() }
        return getModels()
    }

    /**
     * Reads cached ArliAI checkpoint metadata.
     *
     * @return locally stored ArliAI checkpoints.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getModels(): List<StableDiffusionModel> =
        localDataSource.getModels()
}
