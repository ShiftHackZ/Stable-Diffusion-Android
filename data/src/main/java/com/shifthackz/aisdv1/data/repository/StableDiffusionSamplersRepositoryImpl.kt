package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionSamplersRepositoryImpl(
    private val remoteDataSource: StableDiffusionSamplersDataSource.Remote,
    private val localDataSource: StableDiffusionSamplersDataSource.Local,
) : StableDiffusionSamplersRepository {

    override fun fetchSamplers(): Completable = remoteDataSource
        .fetchSamplers()
        .flatMapCompletable(localDataSource::insertSamplers)

    override fun getSamplers(): Single<List<StableDiffusionSampler>> = localDataSource.getSamplers()
}
