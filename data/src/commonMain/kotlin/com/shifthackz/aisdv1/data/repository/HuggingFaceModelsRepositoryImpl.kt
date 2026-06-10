package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

internal class HuggingFaceModelsRepositoryImpl(
    private val remoteDataSource: HuggingFaceModelsRemoteDataSource,
    private val localDataSource: HuggingFaceModelsDataSource.Local,
) : HuggingFaceModelsRepository {

    override suspend fun fetchHuggingFaceModels() {
        localDataSource.save(remoteDataSource.fetchHuggingFaceModels().supportedHfInferenceModels())
    }

    override suspend fun fetchAndGetHuggingFaceModels(): List<HuggingFaceModel> {
        runCatching { fetchHuggingFaceModels() }
        return getHuggingFaceModels()
    }

    override suspend fun getHuggingFaceModels() = localDataSource.getAll()
        .supportedHfInferenceModels()

    private fun List<HuggingFaceModel>.supportedHfInferenceModels(): List<HuggingFaceModel> {
        val remoteByAlias = filter { model ->
            model.alias in HuggingFaceModel.supportedHfInferenceTextToImageAliases
        }.associateBy(HuggingFaceModel::alias)
        return HuggingFaceModel.supportedHfInferenceTextToImageModels.map { model ->
            remoteByAlias[model.alias] ?: model
        }
    }
}
