package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

/**
 * Defines the `StabilityAiEnginesRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiEnginesRepository {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    suspend fun fetch(apiKey: String): List<StabilityAiEngine>
}
