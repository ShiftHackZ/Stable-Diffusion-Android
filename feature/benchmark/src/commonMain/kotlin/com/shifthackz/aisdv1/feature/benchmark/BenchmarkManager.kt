package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import kotlinx.coroutines.flow.Flow

/**
 * High-level API for the benchmark screen.
 *
 * @author Dmitriy Moroz
 */
class BenchmarkManager internal constructor(
    private val deviceProbe: BenchmarkDeviceProbe,
    private val scoreEngine: BenchmarkScoreEngine,
    private val repository: BenchmarkRepository,
    private val preferenceManager: PreferenceManager,
) {

    fun observeLatest(): Flow<BenchmarkResult?> = repository.observeLatest()

    suspend fun inspectDevice(): BenchmarkDeviceInfo = deviceProbe.capture()

    suspend fun runBenchmark(): BenchmarkResult {
        val result = scoreEngine.score(deviceProbe.capture())
        val saved = repository.save(result)
        preferenceManager.benchmarkRecommendationWarningSuppressed = false
        return saved
    }
}
