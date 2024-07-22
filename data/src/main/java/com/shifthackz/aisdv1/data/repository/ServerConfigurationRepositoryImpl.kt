package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.repository.ServerConfigurationRepository

internal class ServerConfigurationRepositoryImpl(
    private val remoteDataSource: ServerConfigurationDataSource.Remote,
    private val localDataSource: ServerConfigurationDataSource.Local,
) : ServerConfigurationRepository {

    override fun fetchConfiguration() = remoteDataSource
        .fetchConfiguration()
        .flatMapCompletable(localDataSource::save)

    override fun fetchAndGetConfiguration() = fetchConfiguration()
        .onErrorComplete()
        .andThen(getConfiguration())

    override fun getConfiguration() = localDataSource.get()

    override fun updateConfiguration(configuration: ServerConfiguration) = remoteDataSource
        .updateConfiguration(configuration)
}
