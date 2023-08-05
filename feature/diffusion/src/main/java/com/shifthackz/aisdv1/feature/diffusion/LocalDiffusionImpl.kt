package com.shifthackz.aisdv1.feature.diffusion

import ai.onnxruntime.OnnxTensor
import android.graphics.Bitmap
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload
import com.shifthackz.aisdv1.domain.feature.diffusion.LocalDiffusion
import com.shifthackz.aisdv1.feature.diffusion.ai.tokenizer.LocalDiffusionTextTokenizer
import com.shifthackz.aisdv1.feature.diffusion.ai.unet.UNet
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject

internal class LocalDiffusionImpl(
    private val uNet: UNet,
    private val tokenizer: LocalDiffusionTextTokenizer,
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
) : LocalDiffusion {

    private val statusSubject: PublishSubject<LocalDiffusion.Status> = PublishSubject.create()

    override fun process(payload: TextToImagePayload): Single<Bitmap> = Single.create { emitter ->
        try {
            uNet.setCallback(object : UNet.Callback {
                override fun onStep(maxStep: Int, step: Int) {
                    debugLog("[CLB] Step changed: $step/$maxStep")
                    statusSubject.onNext(LocalDiffusion.Status(step, maxStep))
                }

                override fun onBuildImage(status: Int, bitmap: Bitmap?) {
                    debugLog("[CLB] Image built with status: $status, bitmap: $bitmap")
                    if (status < 0) return
                    if (!emitter.isDisposed) {
                        bitmap?.let(emitter::onSuccess) ?: emitter.onError(Throwable("Bitmap is null"))
                    }
                }

                override fun onComplete() {
                    debugLog("[CLB] Completed.")
                }

                override fun onStop() {
                    debugLog("[CLB] Stopped.")
                }
            })

            debugLog("processor start")
            tokenizer.initialize()
            val batch_size = 1

            val num_inference_steps = 8
            val guidance_scale = 5.0

            val textTokenized = tokenizer.encode(payload.prompt)
            val negTokenized = tokenizer.createUnconditionalInput(payload.negativePrompt)

            val textPromptEmbeddings = tokenizer.tensor(textTokenized)
            val uncondEmbedding = tokenizer.tensor(negTokenized)
            val textEmbeddingArray = Array<Array<FloatArray>>(2) {
                Array<FloatArray>(tokenizer.maxLength) {
                    FloatArray(768)
                }
            }

            val textPromptEmbeddingArray = textPromptEmbeddings!!.floatBuffer.array()
            val uncondEmbeddingArray = uncondEmbedding!!.floatBuffer.array()
            for (i in textPromptEmbeddingArray.indices) {
                textEmbeddingArray[0][i / 768][i % 768] = uncondEmbeddingArray[i]
                textEmbeddingArray[1][i / 768][i % 768] = textPromptEmbeddingArray[i]
            }

            val textEmbeddings = OnnxTensor.createTensor(ortEnvironmentProvider.get(), textEmbeddingArray)
            tokenizer.close()

            uNet.initialize()
            uNet.inference(
                payload.seed.toLongOrNull() ?: 0L,
                num_inference_steps,
                textEmbeddings,
                guidance_scale,
                batch_size,
                payload.width,
                payload.height,
            )
            debugLog("processor complete")
//            emitter.onComplete()
        } catch (e: Exception) {
            debugLog("processor error")
            if (!emitter.isDisposed) emitter.onError(e)
        }
    }

    override fun observeStatus(): Observable<LocalDiffusion.Status> {
        return statusSubject//.toFlowable(BackpressureStrategy.LATEST)
    }
}
