package com.shifthackz.aisdv1.feature.bonsai

import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.bonsai.BonsaiDiffusion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implements the iOS entry point for Bonsai Image generation.
 *
 * @author Dmitriy Moroz
 */
internal class BonsaiDiffusionImpl : BonsaiDiffusion {

    private val statusFlow = MutableSharedFlow<LocalDiffusionStatus>(extraBufferCapacity = 64)

    /**
     * Executes the `process` step in the SDAI Bonsai feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String {
        println(
            "[Bonsai] kmp request modelPath=$modelPath " +
                "size=${payload.width}x${payload.height} steps=${payload.samplingSteps} " +
                "cfg=${payload.cfgScale} seedBlank=${payload.seed.isBlank()} " +
                "promptChars=${payload.prompt.length} negativeChars=${payload.negativePrompt.length}",
        )
        statusFlow.tryEmit(LocalDiffusionStatus(0, payload.samplingSteps))
        return SiliconDiffusionBonsaiRuntimeRegistry.generate(
            request = payload.toBonsaiRequest(modelPath),
            onProgress = { progress ->
                println("[Bonsai] kmp progress ${progress.current}/${progress.total}")
                statusFlow.tryEmit(
                    LocalDiffusionStatus(
                        current = progress.current,
                        total = progress.total,
                    ),
                )
            },
        )
    }

    /**
     * Performs the SDAI side effect handled by `interrupt`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interrupt() {
        println("[Bonsai] kmp interrupt")
        SiliconDiffusionBonsaiRuntimeRegistry.interrupt()
        statusFlow.tryEmit(LocalDiffusionStatus(0, 0))
    }

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeStatus() = statusFlow.asSharedFlow()
}

/**
 * Carries generation data into the Swift Bonsai runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionBonsaiRequest(
    val modelPath: String,
    val prompt: String,
    val negativePrompt: String,
    val samplingSteps: Int,
    val cfgScale: Float,
    val width: Int,
    val height: Int,
    val seed: String,
    val batchCount: Int,
    val allowNsfw: Boolean,
)

/**
 * Carries progress updates from the Swift Bonsai runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionBonsaiProgress(
    val current: Int,
    val total: Int,
)

/**
 * Carries generation output from the Swift Bonsai runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionBonsaiResponse(
    val imageBase64: String?,
    val errorMessage: String?,
)

/**
 * Defines the Swift-side Bonsai runtime bridge.
 *
 * @author Dmitriy Moroz
 */
interface SiliconDiffusionBonsaiRuntime {
    fun generate(
        request: SiliconDiffusionBonsaiRequest,
        onProgress: (SiliconDiffusionBonsaiProgress) -> Unit,
        completion: (SiliconDiffusionBonsaiResponse) -> Unit,
    )

    fun interrupt()
}

/**
 * Stores the Swift runtime bridge used by the iOS Bonsai feature implementation.
 *
 * @author Dmitriy Moroz
 */
object SiliconDiffusionBonsaiRuntimeRegistry {

    private var runtime: SiliconDiffusionBonsaiRuntime? = null

    fun register(runtime: SiliconDiffusionBonsaiRuntime) {
        this.runtime = runtime
    }

    fun unregister() {
        runtime = null
    }

    internal suspend fun generate(
        request: SiliconDiffusionBonsaiRequest,
        onProgress: (SiliconDiffusionBonsaiProgress) -> Unit,
    ): String = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        val bridge = runtime
        if (bridge == null) {
            continuation.resumeWithException(
                IllegalStateException("Bonsai Image runtime is not registered."),
            )
            return@suspendCancellableCoroutine
        }

        bridge.generate(
            request = request,
            onProgress = onProgress,
            completion = { response ->
                val image = response.imageBase64
                val error = response.errorMessage
                when {
                    continuation.isActive && image != null -> continuation.resume(image)
                    continuation.isActive -> continuation.resumeWithException(
                        IllegalStateException(error ?: "Bonsai Image generation failed."),
                    )
                }
            },
        )
        continuation.invokeOnCancellation { bridge.interrupt() }
    }

    internal fun interrupt() {
        runtime?.interrupt()
    }
}

private fun TextToImagePayload.toBonsaiRequest(modelPath: String) =
    SiliconDiffusionBonsaiRequest(
        modelPath = modelPath,
        prompt = prompt,
        negativePrompt = negativePrompt,
        samplingSteps = samplingSteps,
        cfgScale = cfgScale,
        width = width,
        height = height,
        seed = seed,
        batchCount = batchCount,
        allowNsfw = nsfw,
    )
