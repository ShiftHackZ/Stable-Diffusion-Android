package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToEntity
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

internal class ServerConfigurationLocalDataSource(
    private val dao: ServerConfigurationDao,
) : ServerConfigurationDataSource.Local {

    override suspend fun save(configuration: ServerConfiguration) =
        dao.insert(configuration.mapToEntity())

    override suspend fun get(): ServerConfiguration = dao
        .query()
        .let(ServerConfigurationEntity::mapToDomain)
}
