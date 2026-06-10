package com.shifthackz.aisdv1.domain.datasource

interface StabilityAiCreditsRemoteDataSource {

    suspend fun fetch(apiKey: String): Float
}
