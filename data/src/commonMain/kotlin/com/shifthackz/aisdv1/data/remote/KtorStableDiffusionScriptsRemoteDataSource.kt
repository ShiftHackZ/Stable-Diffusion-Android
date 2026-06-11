package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapToBasicHttpAuthorization
import com.shifthackz.aisdv1.data.mappers.mapExtensionsToDomain
import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.merge
import com.shifthackz.aisdv1.domain.datasource.StableDiffusionScriptsDataSource
import com.shifthackz.aisdv1.domain.entity.StableDiffusionScripts
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.network.api.automatic1111.Automatic1111MetadataApi

/**
 * Coordinates `KtorStableDiffusionScriptsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class KtorStableDiffusionScriptsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: Automatic1111MetadataApi,
) : StableDiffusionScriptsDataSource {

    /**
     * Loads SDAI data through `fetchScripts`.
     *
     * @param baseUrl base url value consumed by the API.
     * @param credentials credentials value consumed by the API.
     * @return Result produced by `fetchScripts`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchScripts(
        baseUrl: String,
        credentials: AuthorizationCredentials,
    ): StableDiffusionScripts {
        val authorization = credentials.mapToBasicHttpAuthorization()
        val scripts = runCatching {
            api
                .fetchScriptInfo(
                    baseUrl = baseUrl,
                    authorization = authorization,
                )
                .mapToDomain()
        }.getOrElse {
            api
                .fetchScripts(
                    baseUrl = baseUrl,
                    authorization = authorization,
                )
                .mapToDomain()
        }
        val extensions = runCatching {
            api
                .fetchExtensions(
                    baseUrl = baseUrl,
                    authorization = authorization,
                )
                .mapExtensionsToDomain()
        }.getOrDefault(StableDiffusionScripts())

        return scripts.merge(extensions)
    }
}
