package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository
import io.reactivex.rxjava3.core.Single

internal class StabilityAiEnginesRepositoryImpl(
    private val remoteDataSource: StabilityAiEnginesDataSource.Remote,
) : StabilityAiEnginesRepository {

    override fun fetchAndGet(): Single<List<StabilityAiEngine>> = remoteDataSource.fetch()
}
