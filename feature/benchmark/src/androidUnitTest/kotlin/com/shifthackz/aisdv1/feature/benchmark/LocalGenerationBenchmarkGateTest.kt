package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalGenerationBenchmarkGateTest {

    @Test
    fun `given remote generation source, expected ready without benchmark prompt`() = runTest {
        val preferences = preferences(promptAnswered = false)
        val repository = FakeBenchmarkRepository(latest = null)
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(request(source = ServerSource.AUTOMATIC1111))

        assertSame(LocalGenerationGateResult.Ready, result)
    }

    @Test
    fun `given first local generation without result, expected benchmark prompt`() = runTest {
        val preferences = preferences(promptAnswered = false)
        val repository = FakeBenchmarkRepository(latest = null)
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(request())

        assertSame(LocalGenerationGateResult.AskRunBenchmark, result)
    }

    @Test
    fun `given benchmark exists and first prompt is unanswered, expected prompt marked answered`() = runTest {
        val preferences = preferences(promptAnswered = false)
        val repository = FakeBenchmarkRepository(latest = benchmarkResult())
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(request())

        assertSame(LocalGenerationGateResult.Ready, result)
        verify { preferences.localGenerationBenchmarkPromptAnswered = true }
    }

    @Test
    fun `given warning suppressed and request exceeds benchmark, expected ready`() = runTest {
        val preferences = preferences(
            promptAnswered = true,
            warningSuppressed = true,
        )
        val repository = FakeBenchmarkRepository(latest = benchmarkResult())
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(
            request(
                width = 512,
                height = 512,
                samplingSteps = 30,
                batchCount = 3,
                hiresFix = true,
            ),
        )

        assertSame(LocalGenerationGateResult.Ready, result)
    }

    @Test
    fun `given local request exceeds benchmark recommendation, expected confirmation reasons`() = runTest {
        val preferences = preferences(promptAnswered = true)
        val repository = FakeBenchmarkRepository(latest = benchmarkResult())
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(
            request(
                width = 512,
                height = 512,
                samplingSteps = 30,
                batchCount = 3,
                hiresFix = true,
            ),
        )

        assertTrue(result is LocalGenerationGateResult.ConfirmExceedsRecommendation)
        val confirmation = result as LocalGenerationGateResult.ConfirmExceedsRecommendation
        assertEquals(
            listOf(
                BenchmarkLimitExceeded.IMAGE_SIZE,
                BenchmarkLimitExceeded.SAMPLING_STEPS,
                BenchmarkLimitExceeded.BATCH_COUNT,
                BenchmarkLimitExceeded.HIRES_FIX,
            ),
            confirmation.reasons,
        )
    }

    @Test
    fun `given provider is not recommended, expected provider confirmation`() = runTest {
        val preferences = preferences(promptAnswered = true)
        val repository = FakeBenchmarkRepository(latest = benchmarkResult())
        val gate = LocalGenerationBenchmarkGate(preferences, repository)

        val result = gate.evaluate(request(source = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE))

        assertTrue(result is LocalGenerationGateResult.ConfirmExceedsRecommendation)
        val confirmation = result as LocalGenerationGateResult.ConfirmExceedsRecommendation
        assertEquals(listOf(BenchmarkLimitExceeded.PROVIDER), confirmation.reasons)
    }

    private fun preferences(
        promptAnswered: Boolean,
        warningSuppressed: Boolean = false,
    ): PreferenceManager {
        var localPromptAnswered = promptAnswered
        var localWarningSuppressed = warningSuppressed
        return mockk<PreferenceManager>(relaxed = true).apply {
            every { localGenerationBenchmarkPromptAnswered } answers { localPromptAnswered }
            every { localGenerationBenchmarkPromptAnswered = any() } answers {
                localPromptAnswered = firstArg()
            }
            every { benchmarkRecommendationWarningSuppressed } answers { localWarningSuppressed }
            every { benchmarkRecommendationWarningSuppressed = any() } answers {
                localWarningSuppressed = firstArg()
            }
        }
    }

    private fun request(
        source: ServerSource = ServerSource.LOCAL_MICROSOFT_ONNX,
        width: Int = 256,
        height: Int = 256,
        samplingSteps: Int = 8,
        batchCount: Int = 1,
        hiresFix: Boolean = false,
    ) = LocalGenerationRequest(
        source = source,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        batchCount = batchCount,
        hiresFix = hiresFix,
    )

    private fun benchmarkResult() = BenchmarkResult(
        createdAt = 1L,
        deviceInfo = BenchmarkDeviceInfo(
            platform = BenchmarkPlatform.ANDROID,
            manufacturer = "Google",
            model = "Pixel 3a XL",
            osVersion = "Android 12",
            cpuName = "SDM670",
            cpuCores = 8,
            gpuName = "Adreno 615",
            totalRamMb = 3_580,
            availableRamMb = 1_000,
            totalVramMb = null,
            availableVramMb = null,
            accelerators = emptyList(),
        ),
        cpuScore = 1_000,
        memoryScore = 900,
        acceleratorScore = 1_100,
        totalScore = 1_200,
        estimatedTimeSeconds = 270,
        recommendation = recommendation(),
        providerRecommendations = listOf(
            BenchmarkProviderRecommendation(
                provider = ServerSource.LOCAL_MICROSOFT_ONNX,
                recommended = true,
                width = 256,
                height = 256,
                samplingSteps = 8,
                cfgScale = 6f,
                batchCount = 1,
                estimatedTimeSeconds = 270,
                backgroundGeneration = false,
                sdxlBackend = SdxlBackend.CPU,
            ),
            BenchmarkProviderRecommendation(
                provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                recommended = false,
                width = 0,
                height = 0,
                samplingSteps = 0,
                cfgScale = 0f,
                batchCount = 1,
                estimatedTimeSeconds = 0,
                backgroundGeneration = false,
                sdxlBackend = SdxlBackend.CPU,
            ),
        ),
    )

    private fun recommendation() = BenchmarkRecommendation(
        width = 256,
        height = 256,
        samplingSteps = 8,
        cfgScale = 6f,
        batchCount = 1,
        providers = listOf(ServerSource.LOCAL_MICROSOFT_ONNX),
        backgroundGeneration = false,
        sdxlBackend = SdxlBackend.CPU,
    )

    private class FakeBenchmarkRepository(
        latest: BenchmarkResult?,
    ) : BenchmarkRepository {
        private val resultFlow = MutableStateFlow(latest)
        private var latestResult = latest

        override fun observeLatest(): Flow<BenchmarkResult?> = resultFlow

        override suspend fun getLatest(): BenchmarkResult? = latestResult

        override suspend fun save(result: BenchmarkResult): BenchmarkResult {
            latestResult = result
            resultFlow.value = result
            return result
        }
    }
}
