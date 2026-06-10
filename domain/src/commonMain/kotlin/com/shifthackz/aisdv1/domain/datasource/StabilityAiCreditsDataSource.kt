package com.shifthackz.aisdv1.domain.datasource

import kotlinx.coroutines.flow.Flow

sealed interface StabilityAiCreditsDataSource {

    interface Local : StabilityAiCreditsDataSource {
        suspend fun get(): Float
        suspend fun save(value: Float)
        fun observe(): Flow<Float>
    }
}
