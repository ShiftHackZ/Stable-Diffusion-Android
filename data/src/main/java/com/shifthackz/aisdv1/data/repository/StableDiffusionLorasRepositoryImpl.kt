package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StableDiffusionLorasDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import com.shifthackz.aisdv1.domain.repository.StableDiffusionLorasRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class StableDiffusionLorasRepositoryImpl(
    private val remoteDataSource: StableDiffusionLorasDataSource.Remote,
    private val localDataSource: StableDiffusionLorasDataSource.Local,
) : StableDiffusionLorasRepository {

    override fun fetchLoras(): Completable = remoteDataSource
        .fetchLoras()
        .flatMapCompletable(localDataSource::insertLoras)

    override fun fetchAndGetLoras(): Single<List<StableDiffusionLora>> = fetchLoras()
        .onErrorComplete()
        .andThen(getLoras())

    override fun getLoras(): Single<List<StableDiffusionLora>> = localDataSource.getLoras()
}
