package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.HuggingFaceModelsDataSource
import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class HuggingFaceModelsRepositoryImpl(
    private val remoteDataSource: HuggingFaceModelsDataSource.Remote,
    private val localDataSource: HuggingFaceModelsDataSource.Local,
) : HuggingFaceModelsRepository {

    override fun fetchHuggingFaceModels(): Completable = remoteDataSource
        .fetchHuggingFaceModels()
        .concatMapCompletable(localDataSource::save)

    override fun fetchAndGetHuggingFaceModels(): Single<List<HuggingFaceModel>> = fetchHuggingFaceModels()
        .onErrorComplete()
        .andThen(getHuggingFaceModels())

    override fun getHuggingFaceModels(): Single<List<HuggingFaceModel>> = localDataSource.getAll()
}
