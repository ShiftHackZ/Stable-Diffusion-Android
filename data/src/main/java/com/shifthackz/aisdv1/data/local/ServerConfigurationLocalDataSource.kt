package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToEntity
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfiguration
import com.shifthackz.aisdv1.storage.db.cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db.cache.entity.ServerConfigurationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class ServerConfigurationLocalDataSource(
    private val dao: ServerConfigurationDao,
) : ServerConfigurationDataSource.Local {

    override fun save(configuration: ServerConfiguration): Completable = dao
        .insert(configuration.mapToEntity())

    override fun get(): Single<ServerConfiguration> = dao
        .query()
        .map(ServerConfigurationEntity::mapToDomain)
}
