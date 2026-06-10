package com.shifthackz.aisdv1.domain.datasource

/**
 * Defines the `StabilityAiCreditsRemoteDataSource` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiCreditsRemoteDataSource {

    /**
     * Loads SDAI data through `fetch`.
     *
     * @param apiKey api key value consumed by the API.
     * @return Result produced by `fetch`.
     * @author Dmitriy Moroz
     */
    suspend fun fetch(apiKey: String): Float
}
