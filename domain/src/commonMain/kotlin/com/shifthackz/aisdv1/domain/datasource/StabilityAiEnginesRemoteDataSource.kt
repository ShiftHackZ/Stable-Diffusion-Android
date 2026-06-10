package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

interface StabilityAiEnginesRemoteDataSource {

    suspend fun fetch(apiKey: String): List<StabilityAiEngine>
}
