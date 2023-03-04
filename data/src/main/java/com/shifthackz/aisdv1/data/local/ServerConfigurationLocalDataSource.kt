package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapToDomain
import com.shifthackz.aisdv1.data.mappers.mapToEntity
import com.shifthackz.aisdv1.domain.datasource.ServerConfigurationDataSource
import com.shifthackz.aisdv1.domain.entity.ServerConfigurationDomain
import com.shifthackz.aisdv1.storage.db_cache.dao.ServerConfigurationDao
import com.shifthackz.aisdv1.storage.db_cache.entity.ServerConfigurationEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class ServerConfigurationLocalDataSource(
    private val dao: ServerConfigurationDao,
) : ServerConfigurationDataSource.Local {

    override fun save(configuration: ServerConfigurationDomain): Completable = dao
        .insert(configuration.mapToEntity())

    override fun get(): Single<ServerConfigurationDomain> = dao
        .query()
        .map(ServerConfigurationEntity::mapToDomain)
}
