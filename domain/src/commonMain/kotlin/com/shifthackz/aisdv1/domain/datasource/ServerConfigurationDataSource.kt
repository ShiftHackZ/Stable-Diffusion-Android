package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface ServerConfigurationDataSource {

    interface Remote : ServerConfigurationDataSource {
        suspend fun fetchConfiguration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): ServerConfiguration

        suspend fun updateConfiguration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            configuration: ServerConfiguration,
        )
    }

    interface Local : ServerConfigurationDataSource {
        suspend fun save(configuration: ServerConfiguration)
        suspend fun get(): ServerConfiguration
    }
}
