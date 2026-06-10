package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapKtorRawToDomain
import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.data.mappers.mapToKtorRequest
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

/**
 * Coordinates `KtorServerConfigurationRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorServerConfigurationRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: Automatic1111MetadataApi,
) : ServerConfigurationDataSource.Remote {

    /**
     * Loads SDAI data through `fetchConfiguration`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchConfiguration`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchConfiguration(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): ServerConfiguration = api
        .fetchConfiguration(
            baseUrl = baseUrl,
            authorization = credentials.mapToBasicHttpAuthorization(),
        )
        .mapKtorRawToDomain()

    /**
     * Performs the SDAI side effect handled by `updateConfiguration`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
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
