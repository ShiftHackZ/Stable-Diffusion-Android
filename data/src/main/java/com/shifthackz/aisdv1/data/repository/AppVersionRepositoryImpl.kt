package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.AppVersionDataSource
import com.shifthackz.aisdv1.domain.repository.AppVersionRepository

internal class AppVersionRepositoryImpl(
    private val remoteDataSource: AppVersionDataSource.Remote,
    private val localDataSource: AppVersionDataSource.Local,
) : AppVersionRepository {

    override fun getActualVersion() = remoteDataSource.get()

    override fun getLocalVersion() = localDataSource.get()
}
