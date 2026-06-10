package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.data.mappers.mapRawToCheckpointDomain
import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiEnginesApi

/**
 * Coordinates `KtorStabilityAiEnginesRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorStabilityAiEnginesRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: StabilityAiEnginesApi,
) : StabilityAiEnginesRemoteDataSource {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(apiKey: String): List<StabilityAiEngine> =
        api.fetchEngines(apiKey).mapRawToCheckpointDomain()
}
