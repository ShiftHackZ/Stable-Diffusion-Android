package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao
import com.shifthackz.aisdv1.storage.db.persistent.entity.SupporterEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class SupportersLocalDataSource(
    private val dao: SupporterDao,
) : SupportersDataSource.Local {

    override fun save(data: List<Supporter>): Completable = dao
        .deleteAll()
        .andThen(dao.insertList(data.mapDomainToEntity()))

    override fun getAll(): Single<List<Supporter>> = dao
        .queryAll()
        .map(List<SupporterEntity>::mapEntityToDomain)
}
