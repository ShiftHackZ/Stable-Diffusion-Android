package com.shifthackz.aisdv1.network.api.stabilityai

import com.shifthackz.aisdv1.network.model.StabilityAiEngineRaw

interface StabilityAiEnginesApi {

    suspend fun fetchEngines(apiKey: String): List<StabilityAiEngineRaw>
}
