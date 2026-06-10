package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.SupportersDataSource
import com.shifthackz.aisdv1.domain.datasource.SupportersRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.domain.repository.SupportersRepository

internal class SupportersRepositoryImpl(
    private val remoteDataSource: SupportersRemoteDataSource,
    private val localDataSource: SupportersDataSource.Local,
) : SupportersRepository {

    override suspend fun fetchSupporters() {
        localDataSource.save(remoteDataSource.fetch())
    }

    override suspend fun fetchAndGetSupporters(): List<Supporter> {
        runCatching { fetchSupporters() }
        return getSupporters()
    }

    override suspend fun getSupporters(): List<Supporter> = localDataSource.getAll()
}
