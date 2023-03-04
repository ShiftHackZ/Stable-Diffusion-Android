package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionModelsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import com.shifthackz.aisdv1.domain.repository.StableDiffusionModelsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class StableDiffusionModelsRepositoryImpl(
    private val remoteDataSource: StableDiffusionModelsDataSource.Remote,
    private val localDataSource: StableDiffusionModelsDataSource.Local,
) : StableDiffusionModelsRepository {

    override fun fetchModels(): Completable = remoteDataSource
        .fetchSdModels()
        .flatMapCompletable(localDataSource::insertModels)

    override fun fetchAndGetModels(): Single<List<StableDiffusionModelDomain>> = fetchModels()
        .andThen(getModels())

    override fun getModels(): Single<List<StableDiffusionModelDomain>> =
        localDataSource.getModels()
}
