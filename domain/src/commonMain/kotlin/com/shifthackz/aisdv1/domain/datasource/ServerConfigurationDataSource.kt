package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

/**
 * Defines the `ServerConfigurationDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface ServerConfigurationDataSource {

    /**
     * Defines the `Remote` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Remote : ServerConfigurationDataSource {
        /**
         * Loads SDAI data through `fetchConfiguration`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @return Result produced by `fetchConfiguration`.
         * @author Dmitriy Moroz
         */
        suspend fun fetchConfiguration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): ServerConfiguration

        /**
         * Performs the SDAI side effect handled by `updateConfiguration`.
         *
         * @param baseUrl base url value consumed by the API.
         * @param credentials credentials value consumed by the API.
         * @param configuration configuration value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun updateConfiguration(
            baseUrl: String,
            credentials: AuthorizationCredentials,
            configuration: ServerConfiguration,
        )
    }

    /**
     * Defines the `Local` contract for the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    interface Local : ServerConfigurationDataSource {
        /**
         * Performs the SDAI side effect handled by `save`.
         *
         * @param configuration configuration value consumed by the API.
         * @author Dmitriy Moroz
         */
        suspend fun save(configuration: ServerConfiguration)
        /**
         * Loads SDAI data through `get`.
         *
         * @return Result produced by `get`.
         * @author Dmitriy Moroz
         */
        suspend fun get(): ServerConfiguration
    }
}
