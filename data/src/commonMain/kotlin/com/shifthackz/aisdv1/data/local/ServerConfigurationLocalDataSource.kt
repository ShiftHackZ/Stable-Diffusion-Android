package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToEntity
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity

/**
 * Coordinates `ServerConfigurationLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class ServerConfigurationLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: ServerConfigurationDao,
) : ServerConfigurationDataSource.Local {

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param configuration configuration value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun save(configuration: ServerConfiguration) =
        dao.insert(configuration.mapToEntity())

    /**
     * Loads SDAI data through `get`.
     *
     * @return Result produced by `get`.
     * @author Dmitriy Moroz
     */
    override suspend fun get(): ServerConfiguration = dao
        .query()
        .let(ServerConfigurationEntity::mapToDomain)
}
