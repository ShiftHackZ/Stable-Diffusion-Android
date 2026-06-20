package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource

/**
 * Platform family used by benchmark hardware probes.
 *
 * @author Dmitriy Moroz
 */
enum class BenchmarkPlatform {
    ANDROID,
    IOS,
    UNKNOWN,
}

/**
 * Hardware acceleration capabilities relevant for on-device image generation.
 *
 * @author Dmitriy Moroz
 */
enum class BenchmarkAccelerator {
    VULKAN,
    BONSAI_VULKAN,
    OPEN_CL,
    NNAPI,
    METAL,
    CORE_ML,
    NEURAL_ENGINE,
}

/**
 * Runtime support status for an acceleration path used by local generation.
 *
 * API discovery alone is not enough: Android can report Vulkan/OpenCL/NNAPI
 * while stable-diffusion.cpp or MediaPipe cannot actually create a working
 * backend on that device.
 *
 * @author Dmitriy Moroz
 */
enum class BenchmarkAccelerationStatus {
    SUPPORTED,
    BACKEND_UNAVAILABLE,
    NOT_VALIDATED,
    NOT_RECOMMENDED,
    UNAVAILABLE,
}

/**
 * Capability row rendered by the benchmark screen.
 *
 * @author Dmitriy Moroz
 */
data class BenchmarkAccelerationCapability(
    val accelerator: BenchmarkAccelerator,
    val apiDetected: Boolean,
    val status: BenchmarkAccelerationStatus,
)

/**
 * Static hardware snapshot collected without initializing inference runtimes.
 *
 * @author Dmitriy Moroz
 */
data class BenchmarkDeviceInfo(
    val platform: BenchmarkPlatform,
    val manufacturer: String,
    val model: String,
    val osVersion: String,
    val cpuName: String,
    val cpuCores: Int,
    val gpuName: String,
    val totalRamMb: Long,
    val availableRamMb: Long,
    val totalVramMb: Long?,
    val availableVramMb: Long?,
    val accelerators: List<BenchmarkAccelerator>,
    val acceleratorDiagnostics: List<String> = emptyList(),
)

/**
 * Recommended local generation parameters derived from benchmark results.
 *
 * @author Dmitriy Moroz
 */
data class BenchmarkRecommendation(
    val width: Int,
    val height: Int,
    val samplingSteps: Int,
    val cfgScale: Float,
    val batchCount: Int,
    val providers: List<ServerSource>,
    val backgroundGeneration: Boolean,
    val sdxlBackend: SdxlBackend,
)

/**
 * Conservative outcome for a concrete local generation provider.
 *
 * @author Dmitriy Moroz
 */
data class BenchmarkProviderRecommendation(
    val provider: ServerSource,
    val recommended: Boolean,
    val width: Int,
    val height: Int,
    val samplingSteps: Int,
    val cfgScale: Float,
    val batchCount: Int,
    val estimatedTimeSeconds: Int,
    val backgroundGeneration: Boolean,
    val sdxlBackend: SdxlBackend,
    val issues: List<BenchmarkProviderIssue> = emptyList(),
)

/**
 * Machine-readable reasons attached to provider recommendations.
 *
 * @author Dmitriy Moroz
 */
enum class BenchmarkProviderIssue {
    PLATFORM_UNSUPPORTED,
    LOW_MEMORY,
    LOW_SCORE,
    ONNX_SLOW_LOW_MEMORY,
    MEDIAPIPE_UNSTABLE_LOW_MEMORY,
    SDXL_TINY_EXPERIMENTAL_ONLY,
    SDXL_BACKEND_NOT_VALIDATED,
    ACCELERATOR_API_NOT_AVAILABLE,
}

/**
 * Returns acceleration capabilities as runtime-backend support, not raw OS API
 * presence.
 *
 * @author Dmitriy Moroz
 */
fun BenchmarkDeviceInfo.accelerationCapabilities(): List<BenchmarkAccelerationCapability> =
    BenchmarkAccelerator.values().map { accelerator ->
        BenchmarkAccelerationCapability(
            accelerator = accelerator,
            apiDetected = accelerator in accelerators,
            status = accelerationStatus(accelerator),
        )
    }

private fun BenchmarkDeviceInfo.accelerationStatus(
    accelerator: BenchmarkAccelerator,
): BenchmarkAccelerationStatus {
    val apiDetected = accelerator in accelerators
    return when (platform) {
        BenchmarkPlatform.ANDROID -> when (accelerator) {
            BenchmarkAccelerator.BONSAI_VULKAN -> when {
                !apiDetected -> BenchmarkAccelerationStatus.UNAVAILABLE
                else -> BenchmarkAccelerationStatus.SUPPORTED
            }
            BenchmarkAccelerator.VULKAN,
            BenchmarkAccelerator.OPEN_CL -> when {
                !apiDetected -> BenchmarkAccelerationStatus.UNAVAILABLE
                BenchmarkRecommendationPolicy.isPixel3aClass(this) ->
                    BenchmarkAccelerationStatus.BACKEND_UNAVAILABLE
                else -> BenchmarkAccelerationStatus.NOT_VALIDATED
            }
            BenchmarkAccelerator.NNAPI -> when {
                !apiDetected -> BenchmarkAccelerationStatus.UNAVAILABLE
                BenchmarkRecommendationPolicy.isPixel3aClass(this) ||
                    BenchmarkRecommendationPolicy.effectiveRamMb(this) < 4_500L ->
                    BenchmarkAccelerationStatus.NOT_RECOMMENDED
                else -> BenchmarkAccelerationStatus.NOT_VALIDATED
            }
            BenchmarkAccelerator.METAL,
            BenchmarkAccelerator.CORE_ML,
            BenchmarkAccelerator.NEURAL_ENGINE -> BenchmarkAccelerationStatus.UNAVAILABLE
        }
        BenchmarkPlatform.IOS -> when (accelerator) {
            BenchmarkAccelerator.METAL,
            BenchmarkAccelerator.CORE_ML,
            BenchmarkAccelerator.NEURAL_ENGINE ->
                if (apiDetected) {
                    BenchmarkAccelerationStatus.SUPPORTED
                } else {
                    BenchmarkAccelerationStatus.UNAVAILABLE
                }
            BenchmarkAccelerator.VULKAN,
            BenchmarkAccelerator.BONSAI_VULKAN,
            BenchmarkAccelerator.OPEN_CL,
            BenchmarkAccelerator.NNAPI -> BenchmarkAccelerationStatus.UNAVAILABLE
        }
        BenchmarkPlatform.UNKNOWN -> BenchmarkAccelerationStatus.UNAVAILABLE
    }
}

/**
 * Persisted benchmark score with the device snapshot and recommendations.
 *
 * @author Dmitriy Moroz
 */
data class BenchmarkResult(
    val id: Long = 0L,
    val createdAt: Long,
    val deviceInfo: BenchmarkDeviceInfo,
    val cpuScore: Int,
    val memoryScore: Int,
    val acceleratorScore: Int,
    val totalScore: Int,
    val estimatedTimeSeconds: Int,
    val recommendation: BenchmarkRecommendation,
    val providerRecommendations: List<BenchmarkProviderRecommendation> = emptyList(),
    val notes: List<String> = emptyList(),
)

/**
 * Returns the recommendation for a concrete local provider.
 *
 * @param source local provider selected by the user.
 * @return provider recommendation or null when the benchmark cannot assess it.
 * @author Dmitriy Moroz
 */
fun BenchmarkResult.providerRecommendation(
    source: ServerSource,
): BenchmarkProviderRecommendation? =
    providerRecommendations.firstOrNull { it.provider == source }

/**
 * Compact generation request used for benchmark policy checks.
 *
 * @author Dmitriy Moroz
 */
data class LocalGenerationRequest(
    val source: ServerSource,
    val width: Int,
    val height: Int,
    val samplingSteps: Int,
    val batchCount: Int,
    val hiresFix: Boolean = false,
    val sdxlBackend: SdxlBackend = SdxlBackend.AUTO,
)

/**
 * Reasons why a local request exceeds benchmark recommendations.
 *
 * @author Dmitriy Moroz
 */
enum class BenchmarkLimitExceeded {
    IMAGE_SIZE,
    SAMPLING_STEPS,
    BATCH_COUNT,
    HIRES_FIX,
    PROVIDER,
    BACKEND,
}

/**
 * Decision returned before starting local generation.
 *
 * @author Dmitriy Moroz
 */
sealed interface LocalGenerationGateResult {
    data object Ready : LocalGenerationGateResult
    data object AskRunBenchmark : LocalGenerationGateResult
    data class ConfirmExceedsRecommendation(
        val reasons: List<BenchmarkLimitExceeded>,
        val recommendation: BenchmarkRecommendation,
    ) : LocalGenerationGateResult
}
