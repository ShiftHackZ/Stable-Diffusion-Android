package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.data.mappers.mapDomainToEntity
import com.shifthackz.aisdv1.data.mappers.mapEntityToDomain
import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.storage.db.persistent.dao.SupporterDao

/**
 * Coordinates `SupportersLocalDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class SupportersLocalDataSource(
    /**
     * Exposes the `dao` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val dao: SupporterDao,
) : SupportersDataSource.Local {

    /**
     * Performs the SDAI side effect handled by `save`.
     *
     * @param data data value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend fun save(data: List<Supporter>) {
        dao.deleteAll()
        dao.insertList(data.mapDomainToEntity())
    }

    /**
     * Loads SDAI data through `getAll`.
     *
     * @return Result produced by `getAll`.
     * @author Dmitriy Moroz
     */
    override suspend fun getAll(): List<Supporter> = dao
        .queryAll()
        .mapEntityToDomain()
}
