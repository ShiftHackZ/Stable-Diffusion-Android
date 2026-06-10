package com.shifthackz.aisdv1.feature.onnx

import ai.onnxruntime.OnnxTensor
import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.TAG
import com.shifthackz.aisdv1.feature.onnx.ai.tokenizer.LocalDiffusionTextTokenizer
import com.shifthackz.aisdv1.feature.onnx.ai.unet.UNet
import com.shifthackz.aisdv1.feature.onnx.environment.OrtEnvironmentProvider
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Implements `LocalDiffusion` behavior in the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal class LocalDiffusionImpl(
    /**
     * Exposes the `uNet` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val uNet: UNet,
    /**
     * Exposes the `tokenizer` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val tokenizer: LocalDiffusionTextTokenizer,
    /**
     * Exposes the `ortEnvironmentProvider` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
) : LocalDiffusion {

    /**
     * Exposes the `statusFlow` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val statusFlow = MutableSharedFlow<LocalDiffusionStatus>(extraBufferCapacity = 64)

    /**
     * Executes the `process` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `process`.
     * @author Dmitriy Moroz
     */
    override suspend fun process(payload: TextToImagePayload): Bitmap = suspendCancellableCoroutine { continuation ->
        try {
            continuation.invokeOnCancellation {
                debugLog(TAG, "{$TAG} Received cancelable signal.")
                interruptGeneration()
            }
            uNet.setCallback(object : UNet.Callback {
                override fun onStep(maxStep: Int, step: Int) {
                    debugLog(TAG, "Received step update: ${maxStep}/${step}")
                    statusFlow.tryEmit(LocalDiffusionStatus(step, maxStep))
                }

                override fun onBuildImage(status: Int, bitmap: Bitmap?) {
                    if (!continuation.isActive) return
                    if (bitmap != null) {
                        debugLog("{$TAG} Bitmap built successfully!")
                        continuation.resume(bitmap)
                    } else {
                        val t = Throwable("Bitmap is null")
                        errorLog(t, "{$TAG} Bitmap is null.")
                        continuation.resumeWithException(t)
                    }
                }
            })

            tokenizer.initialize()
            val batchSize = 1

            val numInferenceSteps = payload.samplingSteps
            val guidanceScale = payload.cfgScale.toDouble()

            val textTokenized = tokenizer.encode(payload.prompt)
            val negTokenized = tokenizer.createUnconditionalInput(payload.negativePrompt)

            val textPromptEmbeddings = tokenizer.tensor(textTokenized)
            val unConditionalEmbedding = tokenizer.tensor(negTokenized)
            val textEmbeddingArray = Array(2) {
                Array(tokenizer.maxLength) {
                    FloatArray(768)
                }
            }

            val textPromptEmbeddingArray = textPromptEmbeddings!!.floatBuffer.array()
            val unConditionalEmbeddingArray = unConditionalEmbedding!!.floatBuffer.array()
            for (i in textPromptEmbeddingArray.indices) {
                textEmbeddingArray[0][i / 768][i % 768] = unConditionalEmbeddingArray[i]
                textEmbeddingArray[1][i / 768][i % 768] = textPromptEmbeddingArray[i]
            }

            val textEmbeddings = OnnxTensor.createTensor(ortEnvironmentProvider.get(), textEmbeddingArray)
            tokenizer.close()

            uNet.initialize()
            uNet.inference(
                seedNum = payload.seed.toLongOrNull() ?: 0L,
                numInferenceSteps = numInferenceSteps,
                textEmbeddings = textEmbeddings,
                guidanceScale = guidanceScale,
                batchSize = batchSize,
                width = payload.width,
                height = payload.height,
            )
        } catch (e: Exception) {
            errorLog(e, "{$TAG} Caught exception while Local Diffusion process.")
            interruptGeneration()
            if (continuation.isActive) continuation.resumeWithException(e)
        }
    }

    // ToDo review method of LocalDiffusion cancellation, now next generation crashes using this approach
    /**
     * Performs the SDAI side effect handled by `interrupt`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun interrupt() {
        interruptGeneration()
    }

    /**
     * Loads SDAI data through `observeStatus`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeStatus() = statusFlow.asSharedFlow()

    /**
     * Performs the SDAI side effect handled by `interruptGeneration`.
     *
     * @author Dmitriy Moroz
     */
    private fun interruptGeneration() {
        debugLog("{$TAG} Trying to interrupt generation.")
        tokenizer.close()
        uNet.close()
        debugLog("{$TAG} Generation interrupt successful!")
    }
}
