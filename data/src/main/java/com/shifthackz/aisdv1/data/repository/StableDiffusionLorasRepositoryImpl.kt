package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionLorasDataSource
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository

internal class StableDiffusionLorasRepositoryImpl(
    private val remoteDataSource: StableDiffusionLorasDataSource.Remote,
    private val localDataSource: StableDiffusionLorasDataSource.Local,
) : StableDiffusionLorasRepository {

    override fun fetchLoras() = remoteDataSource
        .fetchLoras()
        .flatMapCompletable(localDataSource::insertLoras)

    override fun fetchAndGetLoras() = fetchLoras()
        .onErrorComplete()
        .andThen(getLoras())

    override fun getLoras() = localDataSource.getLoras()
}
