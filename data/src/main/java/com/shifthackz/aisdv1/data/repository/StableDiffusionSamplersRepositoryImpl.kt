package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionSamplersDataSource
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

internal class StableDiffusionSamplersRepositoryImpl(
    private val remoteDataSource: StableDiffusionSamplersDataSource.Remote,
    private val localDataSource: StableDiffusionSamplersDataSource.Local,
) : StableDiffusionSamplersRepository {

    override fun fetchSamplers() = remoteDataSource
        .fetchSamplers()
        .flatMapCompletable(localDataSource::insertSamplers)

    override fun getSamplers() = localDataSource.getSamplers()
}
