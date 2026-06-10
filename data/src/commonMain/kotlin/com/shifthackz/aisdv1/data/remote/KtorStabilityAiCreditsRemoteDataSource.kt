package com.shifthackz.aisdv1.data.remote

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsRemoteDataSource
import com.shifthackz.aisdv1.network.api.stabilityai.StabilityAiGenerationApi

/**
 * Coordinates `KtorStabilityAiCreditsRemoteDataSource` behavior in the SDAI data layer.
 *
 * @author Dmitriy Moroz
 */
class KtorStabilityAiCreditsRemoteDataSource(
    /**
     * Exposes the `api` value used by the SDAI data layer.
     *
     * @author Dmitriy Moroz
     */
    private val api: StabilityAiGenerationApi,
) : StabilityAiCreditsRemoteDataSource {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    override suspend fun fetch(apiKey: String): Float =
        api.fetchCredits(apiKey).credits ?: 0f
}
