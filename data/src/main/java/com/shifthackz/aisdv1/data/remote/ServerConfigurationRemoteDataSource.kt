package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToRequest
import com.shifthackz.aisdv1.data.provider.ServerUrlProvider
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111RestApi.Companion.PATH_SD_OPTIONS
import com.shifthackz.aisdv1.network.model.ServerConfigurationRaw

internal class ServerConfigurationRemoteDataSource(
    private val serverUrlProvider: ServerUrlProvider,
    private val api: Automatic1111RestApi,
) : ServerConfigurationDataSource.Remote {

    override fun fetchConfiguration() = serverUrlProvider(PATH_SD_OPTIONS)
        .flatMap(api::fetchConfiguration)
        .map(ServerConfigurationRaw::mapToDomain)

    override fun updateConfiguration(configuration: ServerConfiguration) =
        serverUrlProvider(PATH_SD_OPTIONS)
            .flatMapCompletable { url ->
                api.updateConfiguration(url, configuration.mapToRequest())
            }
}
