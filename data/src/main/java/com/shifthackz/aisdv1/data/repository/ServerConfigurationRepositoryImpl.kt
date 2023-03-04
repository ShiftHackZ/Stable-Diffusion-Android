package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfigurationDomain
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class ServerConfigurationRepositoryImpl(
    private val remoteDataSource: ServerConfigurationDataSource.Remote,
    private val localDataSource: ServerConfigurationDataSource.Local,
) : ServerConfigurationRepository {

    override fun fetchConfiguration(): Completable = remoteDataSource
        .fetchConfiguration()
        .flatMapCompletable(localDataSource::save)

    override fun fetchAndGetConfiguration(): Single<ServerConfigurationDomain> =
        fetchConfiguration()
            .andThen(getConfiguration())

    override fun getConfiguration(): Single<ServerConfigurationDomain> = localDataSource.get()

    override fun updateConfiguration(configuration: ServerConfigurationDomain): Completable =
        remoteDataSource.updateConfiguration(configuration)
}
