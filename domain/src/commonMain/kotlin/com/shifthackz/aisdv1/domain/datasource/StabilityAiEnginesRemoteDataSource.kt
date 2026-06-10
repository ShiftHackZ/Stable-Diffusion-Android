package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

/**
 * Defines the `StabilityAiEnginesRemoteDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiEnginesRemoteDataSource {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    suspend fun fetch(apiKey: String): List<StabilityAiEngine>
}
