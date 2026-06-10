package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.RandomImageDataSource
import com.shifthackz.aisdv1.domain.repository.RandomImageRepository

/**
 * Implements `RandomImageRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
internal class RandomImageRepositoryImpl(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: RandomImageDataSource.Remote,
) : RandomImageRepository {

    /**
     * Loads SDAI data through `fetchAndGet`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun fetchAndGet() = remoteDataSource.fetch()
}
