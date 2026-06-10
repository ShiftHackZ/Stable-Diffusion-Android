package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration

/**
 * Defines the `ServerConfigurationRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface ServerConfigurationRepository {
    /**
     * Loads SDAI data through `fetchConfiguration`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchConfiguration()
    /**
     * Loads SDAI data through `fetchAndGetConfiguration`.
     *
     * @return Result produced by `fetchAndGetConfiguration`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetConfiguration(): ServerConfiguration
    /**
     * Loads SDAI data through `getConfiguration`.
     *
     * @return Result produced by `getConfiguration`.
     * @author Dmitriy Moroz
     */
    suspend fun getConfiguration(): ServerConfiguration
    /**
     * Performs the SDAI side effect handled by `updateConfiguration`.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
    suspend fun updateConfiguration(configuration: ServerConfiguration)
}
