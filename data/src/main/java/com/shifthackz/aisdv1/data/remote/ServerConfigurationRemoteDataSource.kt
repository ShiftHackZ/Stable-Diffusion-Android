package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.api.StableDiffusionWebUiAutomaticRestApi
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class ServerConfigurationRemoteDataSource(
    private val api: StableDiffusionWebUiAutomaticRestApi,
) : ServerConfigurationDataSource.Remote {

    override fun fetchConfiguration(): Single<ServerConfiguration> = api
        .fetchConfiguration()
        .map(ServerConfigurationRaw::mapToDomain)

    override fun updateConfiguration(configuration: ServerConfiguration): Completable = api
        .updateConfiguration(configuration.mapToRequest())
}
