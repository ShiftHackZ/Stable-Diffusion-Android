package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Enforces benchmark-related business dialogs before local generation starts.
 *
 * @author Dmitriy Moroz
 */
class LocalGenerationBenchmarkGate(
    private val preferenceManager: PreferenceManager,
    private val repository: BenchmarkRepository,
) {

    suspend fun evaluate(request: LocalGenerationRequest): LocalGenerationGateResult {
        if (!request.source.isLocalGenerationSource()) return LocalGenerationGateResult.Ready
        val latest = repository.getLatest()
        if (!preferenceManager.localGenerationBenchmarkPromptAnswered && latest == null) {
            return LocalGenerationGateResult.AskRunBenchmark
        } else if (!preferenceManager.localGenerationBenchmarkPromptAnswered) {
            preferenceManager.localGenerationBenchmarkPromptAnswered = true
        }
        if (preferenceManager.benchmarkRecommendationWarningSuppressed) {
            return LocalGenerationGateResult.Ready
        }
        latest ?: return LocalGenerationGateResult.Ready
        val providerRecommendation = latest.providerRecommendation(request.source)
        val recommendation = providerRecommendation?.toRecommendation() ?: latest.recommendation
        val exceeded = request.exceeded(recommendation, providerRecommendation)
        return if (exceeded.isEmpty()) {
            LocalGenerationGateResult.Ready
        } else {
            LocalGenerationGateResult.ConfirmExceedsRecommendation(
                reasons = exceeded,
                recommendation = recommendation,
            )
        }
    }

    fun markFirstBenchmarkPromptAnswered() {
        preferenceManager.localGenerationBenchmarkPromptAnswered = true
    }

    fun suppressRecommendationWarnings() {
        preferenceManager.benchmarkRecommendationWarningSuppressed = true
    }

    private fun LocalGenerationRequest.exceeded(
        recommendation: BenchmarkRecommendation,
        providerRecommendation: BenchmarkProviderRecommendation?,
    ): List<BenchmarkLimitExceeded> = buildList {
        if (providerRecommendation != null && !providerRecommendation.recommended) {
            add(BenchmarkLimitExceeded.PROVIDER)
            return@buildList
        }
        if (width * height > recommendation.width * recommendation.height) {
            add(BenchmarkLimitExceeded.IMAGE_SIZE)
        }
        if (samplingSteps > recommendation.samplingSteps) {
            add(BenchmarkLimitExceeded.SAMPLING_STEPS)
        }
        if (batchCount > recommendation.batchCount) {
            add(BenchmarkLimitExceeded.BATCH_COUNT)
        }
        if (hiresFix) {
            add(BenchmarkLimitExceeded.HIRES_FIX)
        }
        if (source !in recommendation.providers && recommendation.providers.isNotEmpty()) {
            add(BenchmarkLimitExceeded.PROVIDER)
        }
        if (
            source == ServerSource.LOCAL_STABLE_DIFFUSION_CPP &&
            sdxlBackend != recommendation.sdxlBackend &&
            sdxlBackend != com.shifthackz.aisdv1.domain.entity.SdxlBackend.AUTO
        ) {
            add(BenchmarkLimitExceeded.BACKEND)
        }
    }

    private fun BenchmarkProviderRecommendation.toRecommendation(): BenchmarkRecommendation =
        BenchmarkRecommendation(
            width = width.takeIf { it > 0 } ?: 256,
            height = height.takeIf { it > 0 } ?: 256,
            samplingSteps = samplingSteps.takeIf { it > 0 } ?: 8,
            cfgScale = cfgScale.takeIf { it > 0f } ?: 6f,
            batchCount = batchCount.takeIf { it > 0 } ?: 1,
            providers = if (recommended) listOf(provider) else emptyList(),
            backgroundGeneration = backgroundGeneration,
            sdxlBackend = sdxlBackend,
        )
}

/**
 * Returns true when the source performs local on-device generation.
 *
 * @author Dmitriy Moroz
 */
fun ServerSource.isLocalGenerationSource(): Boolean =
    this == ServerSource.LOCAL_MICROSOFT_ONNX ||
        this == ServerSource.LOCAL_GOOGLE_MEDIA_PIPE ||
        this == ServerSource.LOCAL_STABLE_DIFFUSION_CPP ||
        this == ServerSource.LOCAL_APPLE_CORE_ML ||
        this == ServerSource.LOCAL_APPLE_BONSAI
