package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao

internal class SupportersLocalDataSource(
    private val dao: SupporterDao,
) : SupportersDataSource.Local {

    override suspend fun save(data: List<Supporter>) {
        dao.deleteAll()
        dao.insertList(data.mapDomainToEntity())
    }

    override suspend fun getAll(): List<Supporter> = dao
        .queryAll()
        .mapEntityToDomain()
}
