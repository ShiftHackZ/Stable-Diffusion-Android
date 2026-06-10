package com.shifthackz.aisdv1.domain.repository

import kotlinx.coroutines.flow.Flow

interface StabilityAiCreditsRepository {
    suspend fun fetch()
    suspend fun fetchAndGet(): Float
    fun fetchAndObserve(): Flow<Float>
    suspend fun get(): Float
    fun observe(): Flow<Float>
}
