package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

/**
 * Defines the `StabilityAiEnginesApi` contract for the SDAI network layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiEnginesApi {

    /**
     * Loads SDAI data through `fetchEngines`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetchEngines`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchEngines(apiKey: String): List<StabilityAiEngineRaw>
}
