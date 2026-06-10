package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.ServerConfiguration

interface ServerConfigurationRepository {
    suspend fun fetchConfiguration()
    suspend fun fetchAndGetConfiguration(): ServerConfiguration
    suspend fun getConfiguration(): ServerConfiguration
    suspend fun updateConfiguration(configuration: ServerConfiguration)
}
