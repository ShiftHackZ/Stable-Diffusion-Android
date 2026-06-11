package com.shifthackz.aisdv1.feature.coreml

import com.shifthackz.aisdv1.domain.entity.ImageToImagePayload
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.coreml.CoreMlDiffusion
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implements the iOS entry point for Silicon Diffusion Core ML generation.
 *
 * @author Dmitriy Moroz
 */
internal class CoreMlDiffusionImpl : CoreMlDiffusion {

    private val statusFlow = MutableSharedFlow<LocalDiffusionStatus>(extraBufferCapacity = 64)

    /**
     * Executes the `process` step in the SDAI Core ML feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: TextToImagePayload,
        modelPath: String,
    ): String {
        statusFlow.tryEmit(LocalDiffusionStatus(0, payload.samplingSteps))
        return SiliconDiffusionCoreMlRuntimeRegistry.generate(
            request = payload.toCoreMlRequest(modelPath),
            onProgress = { progress ->
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
     * Executes the `process` step in the SDAI Core ML feature layer.
     *
     * @param payload generation payload used by the operation.
     * @param modelPath local model directory selected by the user.
     * @author Dmitriy Moroz
     */
    override suspend fun process(
        payload: ImageToImagePayload,
        modelPath: String,
    ): String {
        statusFlow.tryEmit(LocalDiffusionStatus(0, payload.samplingSteps))
        return SiliconDiffusionCoreMlRuntimeRegistry.generate(
            request = payload.toCoreMlRequest(modelPath),
            onProgress = { progress ->
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
        SiliconDiffusionCoreMlRuntimeRegistry.interrupt()
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
 * Carries generation data into the Swift Silicon Diffusion Core ML runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionCoreMlRequest(
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
    val startingImageBase64: String?,
    val strength: Float,
)

/**
 * Carries progress updates from the Swift Silicon Diffusion Core ML runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionCoreMlProgress(
    val current: Int,
    val total: Int,
)

/**
 * Carries generation output from the Swift Silicon Diffusion Core ML runtime.
 *
 * @author Dmitriy Moroz
 */
data class SiliconDiffusionCoreMlResponse(
    val imageBase64: String?,
    val errorMessage: String?,
)

/**
 * Defines the Swift-side Silicon Diffusion Core ML runtime bridge.
 *
 * @author Dmitriy Moroz
 */
interface SiliconDiffusionCoreMlRuntime {
    fun generate(
        request: SiliconDiffusionCoreMlRequest,
        onProgress: (SiliconDiffusionCoreMlProgress) -> Unit,
        completion: (SiliconDiffusionCoreMlResponse) -> Unit,
    )

    fun interrupt()
}

/**
 * Stores the Swift runtime bridge used by the iOS Core ML feature implementation.
 *
 * @author Dmitriy Moroz
 */
object SiliconDiffusionCoreMlRuntimeRegistry {

    private var runtime: SiliconDiffusionCoreMlRuntime? = null

    fun register(runtime: SiliconDiffusionCoreMlRuntime) {
        this.runtime = runtime
    }

    fun unregister() {
        runtime = null
    }

    internal suspend fun generate(
        request: SiliconDiffusionCoreMlRequest,
        onProgress: (SiliconDiffusionCoreMlProgress) -> Unit,
    ): String = kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
        val bridge = runtime
        if (bridge == null) {
            continuation.resumeWithException(
                IllegalStateException("Silicon Diffusion Core ML runtime is not registered."),
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
                        IllegalStateException(error ?: "Silicon Diffusion Core ML generation failed."),
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

private fun TextToImagePayload.toCoreMlRequest(modelPath: String) =
    SiliconDiffusionCoreMlRequest(
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
        startingImageBase64 = null,
        strength = 1f,
    )

private fun ImageToImagePayload.toCoreMlRequest(modelPath: String) =
    SiliconDiffusionCoreMlRequest(
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
        startingImageBase64 = base64Image,
        strength = denoisingStrength,
    )
