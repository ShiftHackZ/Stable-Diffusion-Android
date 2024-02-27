package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionEmbeddingsDataSource
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository

internal class StableDiffusionEmbeddingsRepositoryImpl(
    private val remoteDataSource: StableDiffusionEmbeddingsDataSource.Remote,
    private val localDataSource: StableDiffusionEmbeddingsDataSource.Local,
) : StableDiffusionEmbeddingsRepository {

    override fun fetchEmbeddings() = remoteDataSource
        .fetchEmbeddings()
        .flatMapCompletable(localDataSource::insertEmbeddings)

    override fun fetchAndGetEmbeddings() = fetchEmbeddings()
        .onErrorComplete()
        .andThen(localDataSource.getEmbeddings())

    override fun getEmbeddings() = localDataSource.getEmbeddings()
}
