package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.datasource.SupportersRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.domain.repository.SupportersRepository

/**
 * Implements `SupportersRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class SupportersRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: SupportersRemoteDataSource,
    /**
     * Exposes the `localDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val localDataSource: SupportersDataSource.Local,
) : SupportersRepository {

    /**
     * Loads SDAI data through `fetchSupporters`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchSupporters() {
        localDataSource.save(remoteDataSource.fetch())
    }

    /**
     * Loads SDAI data through `fetchAndGetSupporters`.
     *
     * @return Result produced by `fetchAndGetSupporters`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGetSupporters(): List<Supporter> {
        runCatching { fetchSupporters() }
        return getSupporters()
    }

    /**
     * Loads SDAI data through `getSupporters`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun getSupporters(): List<Supporter> = localDataSource.getAll()
}
