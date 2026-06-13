package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ArliAiModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.ArliAiModelsRepository

/**
 * Implements `ArliAiModelsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ArliAiModelsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: ArliAiModelsDataSource.Remote,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: ArliAiModelsDataSource.Local,
    /**
     * Exposes the `preferenceManager` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) : ArliAiModelsRepository {

    /**
     * Loads SDAI data through `fetchModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchModels() {
        val models = remoteDataSource.fetchModels(preferenceManager.arliAiApiKey)
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
