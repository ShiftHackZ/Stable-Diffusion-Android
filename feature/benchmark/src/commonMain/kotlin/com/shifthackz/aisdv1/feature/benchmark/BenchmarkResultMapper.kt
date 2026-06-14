package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.storage.db.persistent.entity.BenchmarkResultEntity

private const val SEPARATOR = "|"

internal fun BenchmarkResultEntity.toDomain(): BenchmarkResult {
    val deviceInfo = BenchmarkDeviceInfo(
        platform = runCatching { BenchmarkPlatform.valueOf(platform) }.getOrDefault(BenchmarkPlatform.UNKNOWN),
        manufacturer = manufacturer,
        model = model,
        osVersion = osVersion,
        cpuName = cpuName,
        cpuCores = cpuCores,
        gpuName = gpuName,
        totalRamMb = totalRamMb,
        availableRamMb = availableRamMb,
        totalVramMb = totalVramMb.takeIf { it > 0L },
        availableVramMb = availableVramMb.takeIf { it > 0L },
        accelerators = accelerators
            .split(SEPARATOR)
            .filter(String::isNotBlank)
            .mapNotNull { value -> runCatching { BenchmarkAccelerator.valueOf(value) }.getOrNull() },
    )
    val providerRecommendations = BenchmarkRecommendationPolicy.providerRecommendations(
        deviceInfo = deviceInfo,
        totalScore = totalScore,
        cpuScore = cpuScore,
        memoryScore = memoryScore,
        acceleratorScore = acceleratorScore,
    )
    val fallbackRecommendation = BenchmarkRecommendationPolicy
        .fallbackRecommendation(providerRecommendations)
        .takeIf { providerRecommendations.isNotEmpty() }
        ?: BenchmarkRecommendation(
            width = recommendedWidth,
            height = recommendedHeight,
            samplingSteps = recommendedSteps,
            cfgScale = recommendedCfg,
            batchCount = recommendedBatch,
            providers = recommendedProviders
                .split(SEPARATOR)
                .filter(String::isNotBlank)
                .map(ServerSource::parse),
            backgroundGeneration = recommendedBackground,
            sdxlBackend = SdxlBackend.parse(recommendedBackend),
        )
    return BenchmarkResult(
        id = id,
        createdAt = createdAt,
        deviceInfo = deviceInfo,
        cpuScore = cpuScore,
        memoryScore = memoryScore,
        acceleratorScore = acceleratorScore,
        totalScore = totalScore,
        estimatedTimeSeconds = providerRecommendations
            .filter { it.recommended && it.estimatedTimeSeconds > 0 }
            .minOfOrNull { it.estimatedTimeSeconds }
            ?: estimatedTimeSeconds,
        recommendation = fallbackRecommendation,
        providerRecommendations = providerRecommendations,
        notes = notes.split(SEPARATOR).filter(String::isNotBlank),
    )
}

internal fun BenchmarkResult.toEntity(): BenchmarkResultEntity = BenchmarkResultEntity(
    id = id,
    createdAt = createdAt,
    platform = deviceInfo.platform.name,
    manufacturer = deviceInfo.manufacturer,
    model = deviceInfo.model,
    osVersion = deviceInfo.osVersion,
    cpuName = deviceInfo.cpuName,
    cpuCores = deviceInfo.cpuCores,
    gpuName = deviceInfo.gpuName,
    totalRamMb = deviceInfo.totalRamMb,
    availableRamMb = deviceInfo.availableRamMb,
    totalVramMb = deviceInfo.totalVramMb ?: 0L,
    availableVramMb = deviceInfo.availableVramMb ?: 0L,
    accelerators = deviceInfo.accelerators.joinToString(SEPARATOR) { it.name },
    cpuScore = cpuScore,
    memoryScore = memoryScore,
    acceleratorScore = acceleratorScore,
    totalScore = totalScore,
    estimatedTimeSeconds = estimatedTimeSeconds,
    recommendedWidth = recommendation.width,
    recommendedHeight = recommendation.height,
    recommendedSteps = recommendation.samplingSteps,
    recommendedCfg = recommendation.cfgScale,
    recommendedBatch = recommendation.batchCount,
    recommendedProviders = recommendation.providers.joinToString(SEPARATOR) { it.key },
    recommendedBackground = recommendation.backgroundGeneration,
    recommendedBackend = recommendation.sdxlBackend.key,
    notes = notes.joinToString(SEPARATOR),
)
