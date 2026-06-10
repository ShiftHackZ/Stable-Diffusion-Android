package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

/**
 * Implements `HuggingFaceModelsRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class HuggingFaceModelsRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: HuggingFaceModelsRemoteDataSource,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: HuggingFaceModelsDataSource.Local,
) : HuggingFaceModelsRepository {

    /**
     * Loads SDAI data through `fetchHuggingFaceModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchHuggingFaceModels() {
        localDataSource.save(remoteDataSource.fetchHuggingFaceModels().supportedHfInferenceModels())
    }

    /**
     * Loads SDAI data through `fetchAndGetHuggingFaceModels`.
     *
     * @return Result produced by `fetchAndGetHuggingFaceModels`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetHuggingFaceModels(): List<HuggingFaceModel> {
        runCatching { fetchHuggingFaceModels() }
        return getHuggingFaceModels()
    }

    /**
     * Loads SDAI data through `getHuggingFaceModels`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getHuggingFaceModels() = localDataSource.getAll()
        .supportedHfInferenceModels()

    /**
     * Executes the `supportedHfInferenceModels` step in the SDAI data layer.
     *
     * @return Result produced by `supportedHfInferenceModels`.
     * @author Dmitriy Moroz
     */
    private fun List<HuggingFaceModel>.supportedHfInferenceModels(): List<HuggingFaceModel> {
        val remoteByAlias = filter { model ->
            model.alias in HuggingFaceModel.supportedHfInferenceTextToImageAliases
        }.associateBy(HuggingFaceModel::alias)
        return HuggingFaceModel.supportedHfInferenceTextToImageModels.map { model ->
            remoteByAlias[model.alias] ?: model
        }
    }
}
