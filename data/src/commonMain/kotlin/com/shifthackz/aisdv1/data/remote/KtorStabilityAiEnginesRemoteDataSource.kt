package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiEnginesApi

class KtorStabilityAiEnginesRemoteDataSource(
    private val api: StabilityAiEnginesApi,
) : StabilityAiEnginesRemoteDataSource {

    override suspend fun fetch(apiKey: String): List<StabilityAiEngine> =
        api.fetchEngines(apiKey).mapRawToCheckpointDomain()
}
