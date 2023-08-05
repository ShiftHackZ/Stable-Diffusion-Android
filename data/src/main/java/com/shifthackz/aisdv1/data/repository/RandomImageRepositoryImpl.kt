package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository

internal class RandomImageRepositoryImpl(
    private val remoteDataSource: RandomImageDataSource.Remote,
) : RandomImageRepository {

    override fun fetchAndGet() = remoteDataSource.fetch()
}
