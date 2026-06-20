package com.shifthackz.aisdv1.feature.benchmark

import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.entity.SdxlBackend
import com.shifthackz.aisdv1.domain.entity.ServerSource
import kotlin.math.exp
import kotlin.math.max
import kotlin.math.roundToInt
import kotlin.math.sqrt

/**
 * Runs a bounded synthetic benchmark and derives conservative recommendations.
 *
 * The benchmark intentionally avoids model inference and GPU execution, so it
 * cannot crash due to missing model files or unstable AI runtimes.
 *
 * @author Dmitriy Moroz
 */
internal class BenchmarkScoreEngine(
    private val timeProvider: TimeProvider,
) {

    fun score(deviceInfo: BenchmarkDeviceInfo): BenchmarkResult {
        val workload = inferenceLikeScore(deviceInfo)
        val cpu = workload.score
        val memory = memoryScore()
        val accelerator = acceleratorScore(deviceInfo)
        val total = (
            cpu * CPU_WEIGHT +
                memory * MEMORY_WEIGHT +
                accelerator * ACCELERATOR_WEIGHT +
                ramScore(deviceInfo) * RAM_WEIGHT
            )
            .let { weighted -> weighted * deviceClassFactor(deviceInfo) }
            .roundToInt()
            .coerceIn(MIN_TOTAL_SCORE, MAX_TOTAL_SCORE)
        val providerRecommendations = BenchmarkRecommendationPolicy.providerRecommendations(
            deviceInfo = deviceInfo,
            totalScore = total,
            cpuScore = cpu,
            memoryScore = memory,
            acceleratorScore = accelerator,
        )
        val recommendation = BenchmarkRecommendationPolicy.fallbackRecommendation(providerRecommendations)
        return BenchmarkResult(
            createdAt = timeProvider.currentTimeMillis(),
            deviceInfo = deviceInfo,
            cpuScore = cpu,
            memoryScore = memory,
            acceleratorScore = accelerator,
            totalScore = total,
            estimatedTimeSeconds = providerRecommendations
                .filter { it.recommended && it.estimatedTimeSeconds > 0 }
                .minOfOrNull { it.estimatedTimeSeconds }
                ?: BenchmarkRecommendationPolicy.estimateGenerationSeconds(
                    provider = recommendation.providers.firstOrNull()
                        ?: ServerSource.LOCAL_MICROSOFT_ONNX,
                    width = recommendation.width,
                    height = recommendation.height,
                    samplingSteps = recommendation.samplingSteps,
                    totalScore = total,
                    deviceInfo = deviceInfo,
                ),
            recommendation = recommendation,
            providerRecommendations = providerRecommendations,
            notes = notes(deviceInfo, workload),
        )
    }

    private fun inferenceLikeScore(deviceInfo: BenchmarkDeviceInfo): WorkloadScore {
        val workload = InferenceLikeWorkload()
        var checksum = workload.runPass(0)
        var passes = 0
        val targetMs = when {
            BenchmarkRecommendationPolicy.isPixel3aClass(deviceInfo) -> PIXEL_3A_TARGET_MS
            BenchmarkRecommendationPolicy.effectiveRamMb(deviceInfo) < 4_000L -> LOW_MEMORY_TARGET_MS
            else -> DEFAULT_TARGET_MS
        }
        val startedAt = timeProvider.nanoTime()
        var elapsedMs: Double
        do {
            checksum += workload.runPass(passes + 1)
            passes++
            elapsedMs = (timeProvider.nanoTime() - startedAt) / 1_000_000.0
        } while (elapsedMs < targetMs && passes < MAX_WORKLOAD_PASSES)
        if (checksum == Float.NEGATIVE_INFINITY) error("Unreachable")
        val throughput = passes * WORKLOAD_UNITS_PER_PASS / max(elapsedMs, 1.0)
        val coreFactor = sqrt(deviceInfo.cpuCores.coerceAtLeast(1).toDouble()).coerceIn(1.0, 3.2)
        val score = (throughput * INFERENCE_SCORE_SCALE * coreFactor)
            .roundToInt()
            .coerceIn(MIN_COMPONENT_SCORE, MAX_COMPONENT_SCORE)
        return WorkloadScore(
            score = score,
            elapsedMs = elapsedMs.roundToInt(),
            passes = passes,
        )
    }

    private fun memoryScore(): Int {
        val size = MEMORY_BLOCK_MB * 1024 * 1024
        val source = ByteArray(size) { index -> (index % 127).toByte() }
        val target = ByteArray(size)
        var checksum = 0
        val elapsedMs = measureMs {
            repeat(MEMORY_COPY_PASSES) { iteration ->
                source.copyInto(target)
                var index = iteration * MEMORY_STRIDE % MEMORY_STRIDE_WINDOW
                while (index < size) {
                    checksum = checksum xor target[index].toInt()
                    index += MEMORY_STRIDE
                }
            }
        }
        if (checksum == Int.MIN_VALUE) error("Unreachable")
        val mbCopied = size * MEMORY_COPY_PASSES / (1024.0 * 1024.0)
        val mbPerSecond = mbCopied / (max(elapsedMs, 1.0) / 1_000.0)
        return (mbPerSecond * MEMORY_SCORE_SCALE)
            .roundToInt()
            .coerceIn(MIN_COMPONENT_SCORE, MAX_COMPONENT_SCORE)
    }

    private fun acceleratorScore(deviceInfo: BenchmarkDeviceInfo): Int {
        val base = when (deviceInfo.platform) {
            BenchmarkPlatform.ANDROID -> 260
            BenchmarkPlatform.IOS -> 720
            BenchmarkPlatform.UNKNOWN -> 250
        }
        return deviceInfo.accelerationCapabilities()
            .filter { it.status == BenchmarkAccelerationStatus.SUPPORTED }
            .fold(base) { score, capability ->
                score + when (capability.accelerator) {
                    BenchmarkAccelerator.VULKAN -> 180
                    BenchmarkAccelerator.BONSAI_VULKAN -> 220
                    BenchmarkAccelerator.OPEN_CL -> 320
                    BenchmarkAccelerator.NNAPI -> 240
                    BenchmarkAccelerator.METAL -> 760
                    BenchmarkAccelerator.CORE_ML -> 900
                    BenchmarkAccelerator.NEURAL_ENGINE -> 1_200
                }
            }.coerceIn(MIN_COMPONENT_SCORE, MAX_COMPONENT_SCORE)
    }

    private fun ramScore(deviceInfo: BenchmarkDeviceInfo): Int =
        (BenchmarkRecommendationPolicy.effectiveRamMb(deviceInfo) * RAM_SCORE_SCALE)
            .roundToInt()
            .coerceIn(MIN_COMPONENT_SCORE, MAX_COMPONENT_SCORE)

    private fun deviceClassFactor(deviceInfo: BenchmarkDeviceInfo): Float = when {
        BenchmarkRecommendationPolicy.isPixel3aClass(deviceInfo) -> 0.56f
        deviceInfo.platform == BenchmarkPlatform.ANDROID &&
            BenchmarkRecommendationPolicy.effectiveRamMb(deviceInfo) < 4_000L -> 0.72f
        deviceInfo.platform == BenchmarkPlatform.ANDROID -> 0.92f
        deviceInfo.platform == BenchmarkPlatform.IOS -> 1.38f
        else -> 0.75f
    }

    private fun notes(
        deviceInfo: BenchmarkDeviceInfo,
        workload: WorkloadScore,
    ): List<String> = buildList {
        deviceInfo.acceleratorDiagnostics.forEach(::add)
        if (deviceInfo.totalVramMb == null) add("VRAM is not directly exposed by this platform.")
        if (deviceInfo.accelerationCapabilities().none { it.status == BenchmarkAccelerationStatus.SUPPORTED }) {
            add("No supported local hardware acceleration backend was detected.")
        }
        add(
            "CPU score uses an inference-like workload with convolution, normalization, " +
                "attention-style matrix math, and decoder mixing.",
        )
        add("Workload completed ${workload.passes} passes in ${workload.elapsedMs} ms.")
        add("Memory score uses block copy plus strided access.")
        add("No AI runtime, model file, or GPU inference pipeline is initialized during benchmark.")
        if (deviceInfo.accelerationCapabilities().any { it.status == BenchmarkAccelerationStatus.NOT_VALIDATED }) {
            add("Some acceleration APIs are present, but the local runtime backend is not validated for them.")
        }
    }

    private fun measureMs(block: () -> Unit): Double {
        val startedAt = timeProvider.nanoTime()
        block()
        return (timeProvider.nanoTime() - startedAt) / 1_000_000.0
    }

    private companion object {
        private const val CPU_WEIGHT = 0.62f
        private const val MEMORY_WEIGHT = 0.23f
        private const val ACCELERATOR_WEIGHT = 0.05f
        private const val RAM_WEIGHT = 0.10f
        private const val MIN_TOTAL_SCORE = 150
        private const val MAX_TOTAL_SCORE = 12_000
        private const val MIN_COMPONENT_SCORE = 120
        private const val MAX_COMPONENT_SCORE = 8_000
        private const val WORKLOAD_UNITS_PER_PASS = 10_500
        private const val INFERENCE_SCORE_SCALE = 0.095
        private const val DEFAULT_TARGET_MS = 6_000.0
        private const val LOW_MEMORY_TARGET_MS = 5_000.0
        private const val PIXEL_3A_TARGET_MS = 6_500.0
        private const val MAX_WORKLOAD_PASSES = 96
        private const val MEMORY_BLOCK_MB = 8
        private const val MEMORY_COPY_PASSES = 48
        private const val MEMORY_STRIDE = 4096
        private const val MEMORY_STRIDE_WINDOW = MEMORY_STRIDE * 16
        private const val MEMORY_SCORE_SCALE = 1.15
        private const val RAM_SCORE_SCALE = 0.55f
    }
}

private data class WorkloadScore(
    val score: Int,
    val elapsedMs: Int,
    val passes: Int,
)

private data class IosLocalGenerationProfile(
    val size: Int,
    val samplingSteps: Int,
    val estimatedTimeSeconds: Int,
    val backgroundGeneration: Boolean,
)

/**
 * Small deterministic workload shaped like diffusion inference stages.
 *
 * It intentionally uses plain Kotlin arrays, bounded memory, and no model
 * files, GPU contexts, delegates, or native runtime initialization.
 *
 * @author Dmitriy Moroz
 */
private class InferenceLikeWorkload {

    private var input = FloatArray(TENSOR_SIZE) { index ->
        ((index * 31 % 257) - 128) / 128f
    }
    private var output = FloatArray(TENSOR_SIZE)
    private val weights = FloatArray(CHANNELS * CHANNELS * KERNEL_SIZE) { index ->
        ((index * 17 % 37) - 18) / 240f
    }
    private val bias = FloatArray(CHANNELS) { index ->
        ((index % 5) - 2) / 32f
    }
    private val tokens = FloatArray(TOKEN_COUNT * ATTENTION_DIM)
    private val query = FloatArray(TOKEN_COUNT * ATTENTION_DIM)
    private val key = FloatArray(TOKEN_COUNT * ATTENTION_DIM)
    private val value = FloatArray(TOKEN_COUNT * ATTENTION_DIM)
    private val attention = FloatArray(TOKEN_COUNT * TOKEN_COUNT)
    private val context = FloatArray(TOKEN_COUNT * ATTENTION_DIM)
    private val projection = FloatArray(ATTENTION_DIM * ATTENTION_DIM) { index ->
        ((index * 13 % 29) - 14) / 180f
    }

    fun runPass(seed: Int): Float {
        var checksum = 0f
        repeat(RESIDUAL_BLOCKS) { block ->
            convolution3x3()
            groupNormSiluResidual(block)
            swapBuffers()
            checksum += input[(seed * 977 + block * 131) % input.size]
        }
        checksum += attentionBlock(seed)
        checksum += decoderMix(seed)
        return checksum
    }

    private fun convolution3x3() {
        for (channel in 0 until CHANNELS) {
            val offset = channel * PLANE
            for (index in 0 until PLANE) output[offset + index] = 0f
        }
        for (outChannel in 0 until CHANNELS) {
            val outputOffset = outChannel * PLANE
            for (y in 1 until LATENT_SIZE - 1) {
                val row = y * LATENT_SIZE
                for (x in 1 until LATENT_SIZE - 1) {
                    var sum = bias[outChannel]
                    for (inChannel in 0 until CHANNELS) {
                        val inputOffset = inChannel * PLANE
                        val weightOffset = (outChannel * CHANNELS + inChannel) * KERNEL_SIZE
                        sum += input[inputOffset + row - LATENT_SIZE + x - 1] * weights[weightOffset]
                        sum += input[inputOffset + row - LATENT_SIZE + x] * weights[weightOffset + 1]
                        sum += input[inputOffset + row - LATENT_SIZE + x + 1] * weights[weightOffset + 2]
                        sum += input[inputOffset + row + x - 1] * weights[weightOffset + 3]
                        sum += input[inputOffset + row + x] * weights[weightOffset + 4]
                        sum += input[inputOffset + row + x + 1] * weights[weightOffset + 5]
                        sum += input[inputOffset + row + LATENT_SIZE + x - 1] * weights[weightOffset + 6]
                        sum += input[inputOffset + row + LATENT_SIZE + x] * weights[weightOffset + 7]
                        sum += input[inputOffset + row + LATENT_SIZE + x + 1] * weights[weightOffset + 8]
                    }
                    output[outputOffset + row + x] = sum
                }
            }
        }
    }

    private fun groupNormSiluResidual(block: Int) {
        val channelsPerGroup = CHANNELS / GROUPS
        for (group in 0 until GROUPS) {
            val startChannel = group * channelsPerGroup
            val endChannel = startChannel + channelsPerGroup
            var sum = 0f
            var count = 0
            for (channel in startChannel until endChannel) {
                val offset = channel * PLANE
                for (index in 0 until PLANE) {
                    sum += output[offset + index]
                    count++
                }
            }
            val mean = sum / count
            var variance = 0f
            for (channel in startChannel until endChannel) {
                val offset = channel * PLANE
                for (index in 0 until PLANE) {
                    val delta = output[offset + index] - mean
                    variance += delta * delta
                }
            }
            val inverseStd = (1.0 / sqrt((variance / count + EPSILON).toDouble())).toFloat()
            for (channel in startChannel until endChannel) {
                val offset = channel * PLANE
                for (index in 0 until PLANE) {
                    val position = offset + index
                    val normalized = (output[position] - mean) * inverseStd
                    val activated = normalized / (1f + exp((-normalized).toDouble()).toFloat())
                    output[position] = input[position] * RESIDUAL_WEIGHT + activated * (1f - RESIDUAL_WEIGHT) +
                        block * 0.000_13f
                }
            }
        }
    }

    private fun attentionBlock(seed: Int): Float {
        sampleTokens(seed)
        project(tokens, query, 0)
        project(tokens, key, 7)
        project(tokens, value, 13)
        val scale = (1.0 / sqrt(ATTENTION_DIM.toDouble())).toFloat()
        for (token in 0 until TOKEN_COUNT) {
            var rowMax = -Float.MAX_VALUE
            for (other in 0 until TOKEN_COUNT) {
                var dot = 0f
                val queryOffset = token * ATTENTION_DIM
                val keyOffset = other * ATTENTION_DIM
                for (dim in 0 until ATTENTION_DIM) {
                    dot += query[queryOffset + dim] * key[keyOffset + dim]
                }
                val score = dot * scale
                attention[token * TOKEN_COUNT + other] = score
                if (score > rowMax) rowMax = score
            }
            var normalizer = 0f
            val attentionOffset = token * TOKEN_COUNT
            for (other in 0 until TOKEN_COUNT) {
                val probability = exp((attention[attentionOffset + other] - rowMax).toDouble()).toFloat()
                attention[attentionOffset + other] = probability
                normalizer += probability
            }
            val inverseNormalizer = 1f / normalizer
            val contextOffset = token * ATTENTION_DIM
            for (dim in 0 until ATTENTION_DIM) context[contextOffset + dim] = 0f
            for (other in 0 until TOKEN_COUNT) {
                val probability = attention[attentionOffset + other] * inverseNormalizer
                val valueOffset = other * ATTENTION_DIM
                for (dim in 0 until ATTENTION_DIM) {
                    context[contextOffset + dim] += probability * value[valueOffset + dim]
                }
            }
        }
        var checksum = 0f
        for (token in 0 until TOKEN_COUNT) {
            for (dim in 0 until ATTENTION_DIM) {
                val channel = dim % CHANNELS
                val y = (token * 5 + dim * 3 + seed) % LATENT_SIZE
                val x = (token * 3 + dim * 7 + seed) % LATENT_SIZE
                val tensorIndex = channel * PLANE + y * LATENT_SIZE + x
                val value = context[token * ATTENTION_DIM + dim]
                input[tensorIndex] += value * ATTENTION_WRITE_WEIGHT
                checksum += value
            }
        }
        return checksum
    }

    private fun sampleTokens(seed: Int) {
        for (token in 0 until TOKEN_COUNT) {
            for (dim in 0 until ATTENTION_DIM) {
                val channel = dim % CHANNELS
                val y = (token * 7 + dim * 5 + seed) % LATENT_SIZE
                val x = (token * 11 + dim * 3 + seed) % LATENT_SIZE
                tokens[token * ATTENTION_DIM + dim] = input[channel * PLANE + y * LATENT_SIZE + x]
            }
        }
    }

    private fun project(
        source: FloatArray,
        target: FloatArray,
        shift: Int,
    ) {
        for (token in 0 until TOKEN_COUNT) {
            val tokenOffset = token * ATTENTION_DIM
            for (dim in 0 until ATTENTION_DIM) {
                var sum = 0f
                for (inner in 0 until ATTENTION_DIM) {
                    val projectionIndex = ((dim + shift) % ATTENTION_DIM) * ATTENTION_DIM + inner
                    sum += source[tokenOffset + inner] * projection[projectionIndex]
                }
                target[tokenOffset + dim] = sum
            }
        }
    }

    private fun decoderMix(seed: Int): Float {
        var checksum = 0f
        for (channel in 0 until CHANNELS) {
            val offset = channel * PLANE
            for (y in 1 until LATENT_SIZE - 1) {
                val row = y * LATENT_SIZE
                for (x in 1 until LATENT_SIZE - 1) {
                    val index = offset + row + x
                    val mixed = input[index] * 0.62f +
                        input[index - 1] * 0.11f +
                        input[index + 1] * 0.11f +
                        input[index - LATENT_SIZE] * 0.08f +
                        input[index + LATENT_SIZE] * 0.08f
                    output[index] = mixed
                    if ((index + seed) % CHECKSUM_STRIDE == 0) checksum += mixed
                }
            }
        }
        swapBuffers()
        return checksum
    }

    private fun swapBuffers() {
        val previous = input
        input = output
        output = previous
    }

    private companion object {
        private const val LATENT_SIZE = 32
        private const val CHANNELS = 16
        private const val PLANE = LATENT_SIZE * LATENT_SIZE
        private const val TENSOR_SIZE = CHANNELS * PLANE
        private const val KERNEL_SIZE = 9
        private const val GROUPS = 4
        private const val RESIDUAL_BLOCKS = 4
        private const val TOKEN_COUNT = 96
        private const val ATTENTION_DIM = 32
        private const val EPSILON = 1e-5f
        private const val RESIDUAL_WEIGHT = 0.22f
        private const val ATTENTION_WRITE_WEIGHT = 0.000_7f
        private const val CHECKSUM_STRIDE = 251
    }
}

/**
 * Derives provider-specific settings from benchmark measurements.
 *
 * @author Dmitriy Moroz
 */
internal object BenchmarkRecommendationPolicy {

    fun providerRecommendations(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        cpuScore: Int,
        memoryScore: Int,
        acceleratorScore: Int,
    ): List<BenchmarkProviderRecommendation> = when (deviceInfo.platform) {
        BenchmarkPlatform.ANDROID -> androidRecommendations(
            deviceInfo = deviceInfo,
            totalScore = totalScore,
            cpuScore = cpuScore,
            memoryScore = memoryScore,
            acceleratorScore = acceleratorScore,
        )
        BenchmarkPlatform.IOS -> iosRecommendations(deviceInfo, totalScore)
        BenchmarkPlatform.UNKNOWN -> emptyList()
    }

    fun fallbackRecommendation(
        providerRecommendations: List<BenchmarkProviderRecommendation>,
    ): BenchmarkRecommendation {
        val recommended = providerRecommendations.filter(BenchmarkProviderRecommendation::recommended)
        val primary = recommended.firstOrNull()
        return BenchmarkRecommendation(
            width = primary?.width?.takeIf { it > 0 } ?: 256,
            height = primary?.height?.takeIf { it > 0 } ?: 256,
            samplingSteps = primary?.samplingSteps?.takeIf { it > 0 } ?: 8,
            cfgScale = primary?.cfgScale?.takeIf { it > 0f } ?: 6f,
            batchCount = primary?.batchCount?.takeIf { it > 0 } ?: 1,
            providers = recommended.map(BenchmarkProviderRecommendation::provider),
            backgroundGeneration = primary?.backgroundGeneration ?: false,
            sdxlBackend = recommended
                .firstOrNull { it.provider == ServerSource.LOCAL_STABLE_DIFFUSION_CPP }
                ?.sdxlBackend
                ?: SdxlBackend.CPU,
        )
    }

    fun estimateGenerationSeconds(
        provider: ServerSource,
        width: Int,
        height: Int,
        samplingSteps: Int,
        totalScore: Int,
        deviceInfo: BenchmarkDeviceInfo,
    ): Int {
        when (provider) {
            ServerSource.LOCAL_APPLE_CORE_ML -> {
                return estimateCoreMlWarmGenerationSeconds(
                    width = width,
                    height = height,
                    samplingSteps = samplingSteps,
                    totalScore = totalScore,
                    deviceInfo = deviceInfo,
                )
            }
            ServerSource.LOCAL_APPLE_BONSAI -> {
                return estimateBonsaiWarmGenerationSeconds(
                    width = width,
                    height = height,
                    samplingSteps = samplingSteps,
                    totalScore = totalScore,
                    deviceInfo = deviceInfo,
                )
            }
            else -> Unit
        }
        val pixelFactor = width * height / (512f * 512f)
        val stepFactor = samplingSteps / 20f
        val scoreFactor = 2_400f / totalScore.coerceAtLeast(450)
        val providerFactor = when (provider) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> if (isPixel3aClass(deviceInfo)) 3.8f else 1.7f
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> 1.15f
            ServerSource.LOCAL_STABLE_DIFFUSION_CPP -> if (isPixel3aClass(deviceInfo)) 6.0f else 2.8f
            ServerSource.LOCAL_APPLE_CORE_ML -> 0.62f
            else -> 1.0f
        }
        return (72f * pixelFactor * stepFactor * scoreFactor * providerFactor)
            .roundToInt()
            .coerceIn(8, 900)
    }

    fun effectiveRamMb(deviceInfo: BenchmarkDeviceInfo): Long =
        when {
            deviceInfo.platform == BenchmarkPlatform.IOS && deviceInfo.totalRamMb > 24_000L -> 8_192L
            else -> deviceInfo.totalRamMb
        }.coerceAtLeast(512L)

    fun isPixel3aClass(deviceInfo: BenchmarkDeviceInfo): Boolean {
        val value = listOf(
            deviceInfo.manufacturer,
            deviceInfo.model,
            deviceInfo.cpuName,
            deviceInfo.gpuName,
        ).joinToString(" ").lowercase()
        return "pixel 3a" in value ||
            "sargo" in value ||
            "bonito" in value ||
            "sdm670" in value ||
            "adreno 615" in value
    }

    private fun androidRecommendations(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        cpuScore: Int,
        memoryScore: Int,
        acceleratorScore: Int,
    ): List<BenchmarkProviderRecommendation> {
        val ramMb = effectiveRamMb(deviceInfo)
        val lowMemory = ramMb < 4_000L
        val lowScore = totalScore < 1_450
        val pixel3aClass = isPixel3aClass(deviceInfo)
        val onnx = onnxRecommendation(deviceInfo, totalScore, lowMemory, lowScore, pixel3aClass)
        val mediaPipe = mediaPipeRecommendation(deviceInfo, totalScore, lowMemory, pixel3aClass)
        val sdxl = sdxlRecommendation(
            deviceInfo = deviceInfo,
            totalScore = totalScore,
            cpuScore = cpuScore,
            memoryScore = memoryScore,
            acceleratorScore = acceleratorScore,
            lowMemory = lowMemory,
            pixel3aClass = pixel3aClass,
        )
        return listOf(onnx, mediaPipe, sdxl)
    }

    private fun iosRecommendations(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
    ): List<BenchmarkProviderRecommendation> {
        val ramMb = effectiveRamMb(deviceInfo)
        val hasCoreMl = BenchmarkAccelerator.CORE_ML in deviceInfo.accelerators
        val hasMetal = BenchmarkAccelerator.METAL in deviceInfo.accelerators
        return buildList {
            if (hasCoreMl) {
                val profile = iosCoreMlProfile(
                    deviceInfo = deviceInfo,
                    totalScore = totalScore,
                    ramMb = ramMb,
                )
                add(
                    recommended(
                        provider = ServerSource.LOCAL_APPLE_CORE_ML,
                        width = profile.size,
                        height = profile.size,
                        samplingSteps = profile.samplingSteps,
                        cfgScale = 7f,
                        estimatedTimeSeconds = profile.estimatedTimeSeconds,
                        backgroundGeneration = profile.backgroundGeneration,
                    ),
                )
            } else {
                add(
                    notRecommended(
                        provider = ServerSource.LOCAL_APPLE_CORE_ML,
                        issues = listOf(BenchmarkProviderIssue.ACCELERATOR_API_NOT_AVAILABLE),
                    ),
                )
            }

            if (hasMetal) {
                val profile = iosBonsaiProfile(
                    deviceInfo = deviceInfo,
                    totalScore = totalScore,
                    ramMb = ramMb,
                )
                add(
                    recommended(
                        provider = ServerSource.LOCAL_APPLE_BONSAI,
                        width = profile.size,
                        height = profile.size,
                        samplingSteps = profile.samplingSteps,
                        cfgScale = 7f,
                        estimatedTimeSeconds = profile.estimatedTimeSeconds,
                        backgroundGeneration = profile.backgroundGeneration,
                    ),
                )
            } else {
                add(
                    notRecommended(
                        provider = ServerSource.LOCAL_APPLE_BONSAI,
                        issues = listOf(BenchmarkProviderIssue.ACCELERATOR_API_NOT_AVAILABLE),
                    ),
                )
            }
        }
    }

    private fun iosCoreMlProfile(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        ramMb: Long,
    ): IosLocalGenerationProfile {
        val (size, steps, backgroundGeneration) = when {
            deviceInfo.isHighEndIosCoreMlDevice(ramMb) -> Triple(512, 50, true)
            ramMb >= 6_000L && totalScore >= 3_000 -> Triple(512, 30, true)
            ramMb >= 4_000L -> Triple(384, 20, false)
            else -> Triple(384, 12, false)
        }
        return IosLocalGenerationProfile(
            size = size,
            samplingSteps = steps,
            estimatedTimeSeconds = estimateCoreMlWarmGenerationSeconds(
                width = size,
                height = size,
                samplingSteps = steps,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ),
            backgroundGeneration = backgroundGeneration,
        )
    }

    private fun iosBonsaiProfile(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        ramMb: Long,
    ): IosLocalGenerationProfile {
        val (size, steps, backgroundGeneration) = when {
            deviceInfo.isHighEndIosCoreMlDevice(ramMb) -> Triple(512, 20, true)
            ramMb >= 6_000L && totalScore >= 3_000 -> Triple(512, 16, true)
            ramMb >= 4_000L -> Triple(384, 12, false)
            else -> Triple(384, 8, false)
        }
        return IosLocalGenerationProfile(
            size = size,
            samplingSteps = steps,
            estimatedTimeSeconds = estimateBonsaiWarmGenerationSeconds(
                width = size,
                height = size,
                samplingSteps = steps,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ),
            backgroundGeneration = backgroundGeneration,
        )
    }

    private fun estimateCoreMlWarmGenerationSeconds(
        width: Int,
        height: Int,
        samplingSteps: Int,
        totalScore: Int,
        deviceInfo: BenchmarkDeviceInfo,
    ): Int {
        val ramMb = effectiveRamMb(deviceInfo)
        val pixelFactor = width * height / (512f * 512f)
        val millisPerStepAt512 = when {
            deviceInfo.isHighEndIosCoreMlDevice(ramMb) -> 900f
            totalScore >= 4_200 && ramMb >= 6_000L -> 1_300f
            totalScore >= 3_000 && ramMb >= 5_000L -> 1_600f
            else -> 2_100f
        }
        return (samplingSteps * millisPerStepAt512 * pixelFactor / 1_000f)
            .roundToInt()
            .coerceIn(3, 900)
    }

    private fun estimateBonsaiWarmGenerationSeconds(
        width: Int,
        height: Int,
        samplingSteps: Int,
        totalScore: Int,
        deviceInfo: BenchmarkDeviceInfo,
    ): Int {
        val ramMb = effectiveRamMb(deviceInfo)
        val pixelFactor = width * height / (512f * 512f)
        val millisPerStepAt512 = when {
            deviceInfo.isHighEndIosCoreMlDevice(ramMb) -> 870f
            totalScore >= 4_200 && ramMb >= 6_000L -> 1_050f
            totalScore >= 3_000 && ramMb >= 5_000L -> 1_350f
            else -> 1_900f
        }
        return (samplingSteps * millisPerStepAt512 * pixelFactor / 1_000f)
            .roundToInt()
            .coerceIn(3, 900)
    }

    private fun BenchmarkDeviceInfo.isHighEndIosCoreMlDevice(ramMb: Long): Boolean {
        if (platform != BenchmarkPlatform.IOS || ramMb < 8_000L) return false
        if (BenchmarkAccelerator.METAL !in accelerators ||
            BenchmarkAccelerator.CORE_ML !in accelerators ||
            BenchmarkAccelerator.NEURAL_ENGINE !in accelerators
        ) return false
        val value = listOf(manufacturer, model, cpuName, gpuName)
            .joinToString(" ")
            .lowercase()
        return ramMb >= 10_000L ||
            "a17 pro" in value ||
            "a18" in value ||
            "a19" in value ||
            "iphone 15 pro" in value ||
            "iphone 16 pro" in value ||
            "iphone 17 pro" in value ||
            "m1" in value ||
            "m2" in value ||
            "m3" in value ||
            "m4" in value ||
            "m5" in value
    }

    private fun onnxRecommendation(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        lowMemory: Boolean,
        lowScore: Boolean,
        pixel3aClass: Boolean,
    ): BenchmarkProviderRecommendation {
        val issues = buildList {
            if (lowMemory || pixel3aClass) add(BenchmarkProviderIssue.ONNX_SLOW_LOW_MEMORY)
            if (lowScore && !lowMemory && !pixel3aClass) add(BenchmarkProviderIssue.LOW_SCORE)
        }
        val (size, steps, estimate) = when {
            pixel3aClass || lowMemory || lowScore -> Triple(256, 8, 270)
            totalScore < 2_400 -> Triple(384, 12, estimateGenerationSeconds(
                provider = ServerSource.LOCAL_MICROSOFT_ONNX,
                width = 384,
                height = 384,
                samplingSteps = 12,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ))
            else -> Triple(512, 16, estimateGenerationSeconds(
                provider = ServerSource.LOCAL_MICROSOFT_ONNX,
                width = 512,
                height = 512,
                samplingSteps = 16,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ))
        }
        return recommended(
            provider = ServerSource.LOCAL_MICROSOFT_ONNX,
            width = size,
            height = size,
            samplingSteps = steps,
            cfgScale = 6f,
            estimatedTimeSeconds = estimate,
            backgroundGeneration = false,
            issues = issues,
        )
    }

    private fun mediaPipeRecommendation(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        lowMemory: Boolean,
        pixel3aClass: Boolean,
    ): BenchmarkProviderRecommendation {
        val nnapiStatus = deviceInfo.accelerationStatus(BenchmarkAccelerator.NNAPI)
        val ramMb = effectiveRamMb(deviceInfo)
        if (nnapiStatus == BenchmarkAccelerationStatus.UNAVAILABLE) {
            return notRecommended(
                provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                issues = listOf(BenchmarkProviderIssue.ACCELERATOR_API_NOT_AVAILABLE),
            )
        }
        if (pixel3aClass ||
            lowMemory ||
            ramMb < 4_500L ||
            nnapiStatus == BenchmarkAccelerationStatus.NOT_RECOMMENDED
        ) {
            return notRecommended(
                provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                issues = listOf(BenchmarkProviderIssue.MEDIAPIPE_UNSTABLE_LOW_MEMORY),
            )
        }
        if (totalScore < 2_300) {
            return notRecommended(
                provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                issues = listOf(BenchmarkProviderIssue.LOW_SCORE),
            )
        }
        val size = if (totalScore >= 4_000 && ramMb >= 6_000L) 512 else 384
        val steps = if (size == 512) 20 else 14
        return recommended(
            provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
            width = size,
            height = size,
            samplingSteps = steps,
            cfgScale = 7f,
            estimatedTimeSeconds = estimateGenerationSeconds(
                provider = ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                width = size,
                height = size,
                samplingSteps = steps,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ),
            backgroundGeneration = totalScore >= 3_200,
        )
    }

    private fun sdxlRecommendation(
        deviceInfo: BenchmarkDeviceInfo,
        totalScore: Int,
        cpuScore: Int,
        memoryScore: Int,
        acceleratorScore: Int,
        lowMemory: Boolean,
        pixel3aClass: Boolean,
    ): BenchmarkProviderRecommendation {
        val ramMb = effectiveRamMb(deviceInfo)
        if (pixel3aClass || lowMemory) {
            return notRecommended(
                provider = ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
                sdxlBackend = SdxlBackend.CPU,
                issues = listOf(BenchmarkProviderIssue.SDXL_TINY_EXPERIMENTAL_ONLY),
            )
        }
        if (ramMb < 6_000L || totalScore < 2_200 || cpuScore < 1_200 || memoryScore < 1_200) {
            return notRecommended(
                provider = ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
                sdxlBackend = SdxlBackend.CPU,
                issues = listOf(BenchmarkProviderIssue.LOW_SCORE),
            )
        }
        val backend = when {
            deviceInfo.accelerationStatus(BenchmarkAccelerator.OPEN_CL) == BenchmarkAccelerationStatus.SUPPORTED &&
                ramMb >= 8_000L &&
                totalScore >= 3_800 &&
                acceleratorScore >= 900 -> SdxlBackend.OPEN_CL
            else -> SdxlBackend.CPU
        }
        val size = when {
            ramMb >= 10_000L && totalScore >= 5_200 -> 512
            ramMb >= 8_000L && totalScore >= 3_600 -> 384
            else -> 256
        }
        val steps = when (size) {
            512 -> 18
            384 -> 14
            else -> 10
        }
        val issues = buildList {
            if (BenchmarkAccelerator.VULKAN in deviceInfo.accelerators) {
                add(BenchmarkProviderIssue.SDXL_BACKEND_NOT_VALIDATED)
            }
        }
        return recommended(
            provider = ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
            width = size,
            height = size,
            samplingSteps = steps,
            cfgScale = 6f,
            estimatedTimeSeconds = estimateGenerationSeconds(
                provider = ServerSource.LOCAL_STABLE_DIFFUSION_CPP,
                width = size,
                height = size,
                samplingSteps = steps,
                totalScore = totalScore,
                deviceInfo = deviceInfo,
            ),
            backgroundGeneration = totalScore >= 3_600 && ramMb >= 8_000L,
            sdxlBackend = backend,
            issues = issues,
        )
    }

    private fun recommended(
        provider: ServerSource,
        width: Int,
        height: Int,
        samplingSteps: Int,
        cfgScale: Float,
        estimatedTimeSeconds: Int,
        backgroundGeneration: Boolean,
        batchCount: Int = 1,
        sdxlBackend: SdxlBackend = SdxlBackend.CPU,
        issues: List<BenchmarkProviderIssue> = emptyList(),
    ): BenchmarkProviderRecommendation = BenchmarkProviderRecommendation(
        provider = provider,
        recommended = true,
        width = width,
        height = height,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        batchCount = batchCount,
        estimatedTimeSeconds = estimatedTimeSeconds,
        backgroundGeneration = backgroundGeneration,
        sdxlBackend = sdxlBackend,
        issues = issues,
    )

    private fun notRecommended(
        provider: ServerSource,
        issues: List<BenchmarkProviderIssue>,
        sdxlBackend: SdxlBackend = SdxlBackend.CPU,
    ): BenchmarkProviderRecommendation = BenchmarkProviderRecommendation(
        provider = provider,
        recommended = false,
        width = 0,
        height = 0,
        samplingSteps = 0,
        cfgScale = 0f,
        batchCount = 1,
        estimatedTimeSeconds = 0,
        backgroundGeneration = false,
        sdxlBackend = sdxlBackend,
        issues = issues,
    )

    private fun BenchmarkDeviceInfo.accelerationStatus(
        accelerator: BenchmarkAccelerator,
    ): BenchmarkAccelerationStatus =
        accelerationCapabilities()
            .firstOrNull { it.accelerator == accelerator }
            ?.status
            ?: BenchmarkAccelerationStatus.UNAVAILABLE
}
