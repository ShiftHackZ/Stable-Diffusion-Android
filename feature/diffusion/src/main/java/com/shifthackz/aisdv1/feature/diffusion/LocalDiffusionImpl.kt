package com.shifthackz.aisdv1.feature.diffusion

import ai.onnxruntime.OnnxTensor
import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.entity.LocalDiffusionStatus
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.TAG
import com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer.LocalDiffusionTextTokenizer
import com.shifthackz.aisdv1.feature.diffusion.ai.unet.UNet
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject

internal class LocalDiffusionImpl(
    private val uNet: UNet,
    private val tokenizer: LocalDiffusionTextTokenizer,
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
) : LocalDiffusion {

    private val statusSubject: PublishSubject<LocalDiffusionStatus> = PublishSubject.create()

    override fun process(payload: TextToImagePayload): Single<Bitmap> = Single.create { emitter ->
        try {
            emitter.setCancellable {
                debugLog(TAG, "{$TAG} Received cancelable signal.")
                interruptGeneration()
            }
            uNet.setCallback(object : UNet.Callback {
                override fun onStep(maxStep: Int, step: Int) {
                    debugLog(TAG, "Received step update: ${maxStep}/${step}")
                    statusSubject.onNext(LocalDiffusionStatus(step, maxStep))
                }

                override fun onBuildImage(status: Int, bitmap: Bitmap?) {
                    bitmap
                        ?.let(emitter::onSuccess)
                        ?.also { debugLog("{$TAG} Bitmap built successfully!") }
                        ?: run {
                            val t = Throwable("Bitmap is null")
                            errorLog(t, "{$TAG} Bitmap is null.")
                            emitter.onError(t)
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
            emitter.onError(e)
        }
    }

    // ToDo review method of LocalDiffusion cancellation, now next generation crashes using this approach
    override fun interrupt() = Completable.fromAction {
        interruptGeneration()
    }

    override fun observeStatus() = statusSubject

    private fun interruptGeneration() {
        debugLog("{$TAG} Trying to interrupt generation.")
        tokenizer.close()
        uNet.close()
        debugLog("{$TAG} Generation interrupt successful!")
    }
}
