package com.shifthackz.aisdv1.feature.benchmark

import kotlinx.coroutines.flow.Flow

/**
 * Stores benchmark results in the local persistent database.
 *
 * @author Dmitriy Moroz
 */
interface BenchmarkRepository {
    fun observeLatest(): Flow<BenchmarkResult?>
    suspend fun getLatest(): BenchmarkResult?
    suspend fun save(result: BenchmarkResult): BenchmarkResult
}
