package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.data.mappers.mapToKtorRequest
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

class KtorServerConfigurationRemoteDataSource(
    private val api: Automatic1111MetadataApi,
) : ServerConfigurationDataSource.Remote {

    override suspend fun fetchConfiguration(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): ServerConfiguration = api
        .fetchConfiguration(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToDomain()

    override suspend fun updateConfiguration(
        baseUrl: String,
        credentials: AuthorizationCredentials,
        configuration: ServerConfiguration,
    ) {
        api.updateConfiguration(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
            request = configuration.mapToKtorRequest(),
        )
    }
}
