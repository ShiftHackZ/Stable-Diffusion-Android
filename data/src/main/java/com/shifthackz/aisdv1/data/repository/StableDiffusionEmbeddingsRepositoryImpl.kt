package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionEmbeddingsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionEmbedding
import com.shifthackz.aisdv1.domain.repository.StableDiffusionEmbeddingsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionEmbeddingsRepositoryImpl(
    private val remoteDataSource: StableDiffusionEmbeddingsDataSource.Remote,
    private val localDataSource: StableDiffusionEmbeddingsDataSource.Local,
) : StableDiffusionEmbeddingsRepository {

    override fun fetchEmbeddings(): Completable = remoteDataSource
        .fetchEmbeddings()
        .flatMapCompletable(localDataSource::insertEmbeddings)

    override fun fetchAndGetEmbeddings(): Single<List<StableDiffusionEmbedding>> = fetchEmbeddings()
        .onErrorComplete()
        .andThen(localDataSource.getEmbeddings())

    override fun getEmbeddings(): Single<List<StableDiffusionEmbedding>> = localDataSource.getEmbeddings()
}
