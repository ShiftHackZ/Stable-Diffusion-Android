package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi

class KtorStabilityAiCreditsRemoteDataSource(
    private val api: StabilityAiGenerationApi,
) : StabilityAiCreditsRemoteDataSource {

    override suspend fun fetch(apiKey: String): Float =
        api.fetchCredits(apiKey).credits ?: 0f
}
