package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.MotdDataSource
import com.shifthackz.aisdv1.domain.repository.MotdRepository

internal class MotdRepositoryImpl(
    private val remoteDataSource: MotdDataSource.Remote,
) : MotdRepository {

    override fun fetchMotd() = remoteDataSource.fetch()
}
