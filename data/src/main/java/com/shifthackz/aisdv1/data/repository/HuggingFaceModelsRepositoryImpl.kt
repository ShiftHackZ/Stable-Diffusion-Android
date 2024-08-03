package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

internal class HuggingFaceModelsRepositoryImpl(
    private val remoteDataSource: HuggingFaceModelsDataSource.Remote,
    private val localDataSource: HuggingFaceModelsDataSource.Local,
) : HuggingFaceModelsRepository {

    override fun fetchHuggingFaceModels() = remoteDataSource
        .fetchHuggingFaceModels()
        .concatMapCompletable(localDataSource::save)

    override fun fetchAndGetHuggingFaceModels() = fetchHuggingFaceModels()
        .onErrorComplete()
        .andThen(getHuggingFaceModels())

    override fun getHuggingFaceModels() = localDataSource.getAll()
}
