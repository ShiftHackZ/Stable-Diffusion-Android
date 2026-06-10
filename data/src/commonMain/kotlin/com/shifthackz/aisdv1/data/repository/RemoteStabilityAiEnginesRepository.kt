package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository

class RemoteStabilityAiEnginesRepository(
    private val remoteDataSource: StabilityAiEnginesRemoteDataSource,
) : StabilityAiEnginesRepository {

    override suspend fun fetch(apiKey: String): List<StabilityAiEngine> =
        remoteDataSource.fetch(apiKey)
}
