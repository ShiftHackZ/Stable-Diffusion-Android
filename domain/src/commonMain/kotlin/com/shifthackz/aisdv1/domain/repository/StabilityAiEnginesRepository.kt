package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StabilityAiEngine

interface StabilityAiEnginesRepository {

    suspend fun fetch(apiKey: String): List<StabilityAiEngine>
}
