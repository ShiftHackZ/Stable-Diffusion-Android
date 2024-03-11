package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesDataSource
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository

internal class StabilityAiEnginesRepositoryImpl(
    private val remoteDataSource: StabilityAiEnginesDataSource.Remote,
) : StabilityAiEnginesRepository {

    override fun fetchAndGet() = remoteDataSource.fetch()
}
