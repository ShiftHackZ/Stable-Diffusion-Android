package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiEnginesRemoteDataSource
import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine
import com.shifthackz.aisdv1.domain.repository.StabilityAiEnginesRepository

/**
 * Coordinates `RemoteStabilityAiEnginesRepository` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class RemoteStabilityAiEnginesRepository(
    /**
     * Exposes the `remoteDataSource` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val remoteDataSource: StabilityAiEnginesRemoteDataSource,
) : StabilityAiEnginesRepository {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(apiKey: String): List<StabilityAiEngine> =
        remoteDataSource.fetch(apiKey)
}
