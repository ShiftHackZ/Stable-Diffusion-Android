@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.feature.diffusion.ai.unet

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.SessionOptions
import ai.onnxruntime.providers.NNAPIFlags
import android.graphics.Bitmap
import android.util.Pair
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.KEY_ENCODER_HIDDEN_STATES
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.KEY_LATENT_SAMPLE
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.KEY_SAMPLE
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.KEY_TIME_STEP
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.ORT_KEY_MODEL_FORMAT
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.TAG
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.duplicate
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.getSizes
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.multipleTensorsByFloat
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.splitTensor
import com.shifthackz.aisdv1.feature.diffusion.ai.scheduler.EulerAncestralDiscreteLocalDiffusionScheduler
import com.shifthackz.aisdv1.feature.diffusion.ai.vae.VaeDecoder
import com.shifthackz.aisdv1.feature.diffusion.entity.Array3D
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionTensor
import com.shifthackz.aisdv1.feature.diffusion.environment.DeviceNNAPIFlagProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.extensions.modelPathPrefix
import java.nio.IntBuffer
import java.util.EnumSet
import java.util.Random
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt

internal class UNet(
    private val deviceNNAPIFlagProvider: DeviceNNAPIFlagProvider,
    private val ortEnvironmentProvider: OrtEnvironmentProvider,
    private val fileProviderDescriptor: FileProviderDescriptor,
    private val localModelIdProvider: LocalModelIdProvider,
) {

    private var decoder: VaeDecoder? = null

    private val random = Random()

    private var callback: Callback? = null
    private var session: OrtSession? = null

    private var width = 384
    private var height = 384

    fun initialize() {
        if (session != null) return
        decoder = VaeDecoder(
            ortEnvironmentProvider,
            fileProviderDescriptor,
            localModelIdProvider,
            deviceNNAPIFlagProvider.get(),
        )
        val options = SessionOptions()
        options.addConfigEntry(ORT_KEY_MODEL_FORMAT, ORT)
        if (deviceNNAPIFlagProvider.get() == LocalDiffusionFlag.NN_API.value) {
            options.addNnapi(EnumSet.of(NNAPIFlags.CPU_DISABLED))
        }
        session = ortEnvironmentProvider.get().createSession(
            "${modelPathPrefix(fileProviderDescriptor, localModelIdProvider)}/${LocalDiffusionContract.UNET_MODEL}",
            options
        )
    }

    private fun createUNetModelInput(
        encoderHiddenStates: OnnxTensor,
        sample: OnnxTensor,
        timeStep: OnnxTensor
    ): Map<String, OnnxTensor> {
        val map: MutableMap<String, OnnxTensor> = HashMap()
        map[KEY_ENCODER_HIDDEN_STATES] = encoderHiddenStates
        map[KEY_SAMPLE] = sample
        map[KEY_TIME_STEP] = timeStep
        return map
    }

    private fun generateLatentSample(
        batchSize: Int,
        height: Int,
        width: Int,
        seed: Long,
        initNoiseSigma: Float
    ): LocalDiffusionTensor<*> {
        val random = Random(seed)
        val channels = 4
        val latentsArray = Array(batchSize) {
            Array(channels) {
                Array(height / 8) {
                    FloatArray(width / 8)
                }
            }
        }
        for (i in 0 until batchSize) {
            for (j in 0 until channels) {
                for (k in 0 until (height / 8)) {
                    for (l in 0 until (width / 8)) {
                        val u1 = random.nextDouble()
                        val u2 = random.nextDouble()
                        val radius = sqrt(-2.0f * ln(u1))
                        val theta = 2.0 * Math.PI * u2
                        val standardNormalRand = radius * cos(theta)
                        latentsArray[i][j][k][l] = (standardNormalRand * initNoiseSigma).toFloat()
                    }
                }
            }
        }
        return LocalDiffusionTensor(
            OnnxTensor.createTensor(ortEnvironmentProvider.get(), latentsArray),
            latentsArray,
            longArrayOf(
                batchSize.toLong(),
                channels.toLong(),
                (height / 8).toLong(),
                (width / 8).toLong()
            )
        )
    }

    private fun performGuidance(
        noisePrediction: Array3D<FloatArray>,
        noisePredictionText: Array3D<FloatArray>,
        guidanceScale: Double,
    ) {
        val indexes: LongArray = getSizes(noisePrediction)
        for (i in 0 until indexes[0]) {
            for (j in 0 until indexes[1]) {
                for (k in 0 until indexes[2]) {
                    for (l in 0 until indexes[3]) {
                        noisePrediction[i.toInt()][j.toInt()][k.toInt()][l.toInt()] =
                            noisePrediction[i.toInt()][j.toInt()][k.toInt()][l.toInt()] +
                                    guidanceScale.toFloat() * (noisePredictionText[i.toInt()][j.toInt()][k.toInt()][l.toInt()] -
                                    noisePrediction[i.toInt()][j.toInt()][k.toInt()][l.toInt()])
                    }
                }
            }
        }
    }

    fun inference(
        seedNum: Long,
        numInferenceSteps: Int,
        textEmbeddings: OnnxTensor,
        guidanceScale: Double,
        batchSize: Int,
        width: Int,
        height: Int,
    ) {
        debugLog("{$TAG} {uNet} {inference} Trying to start inference:")
        debugLog("{$TAG} {uNet} {inference} - seed: $seedNum")
        debugLog("{$TAG} {uNet} {inference} - numInferenceSteps: $numInferenceSteps")
        debugLog("{$TAG} {uNet} {inference} - textEmbeddings: $textEmbeddings")
        debugLog("{$TAG} {uNet} {inference} - guidanceScale: $guidanceScale")
        debugLog("{$TAG} {uNet} {inference} - batchSize: $batchSize")
        debugLog("{$TAG} {uNet} {inference} - size: ${width}x${height}")
        this.width = width
        this.height = height
        val localDiffusionScheduler = EulerAncestralDiscreteLocalDiffusionScheduler()
        debugLog("{$TAG} {uNet} {inference} Initialized scheduler: $localDiffusionScheduler")
        val timeSteps: IntArray = localDiffusionScheduler.setTimeSteps(numInferenceSteps)
        val seed = if (seedNum <= 0) random.nextLong() else seedNum
        var latents: LocalDiffusionTensor<*> = generateLatentSample(
            batchSize,
            height,
            width,
            seed,
            localDiffusionScheduler.initNoiseSigma.toFloat()
        )
        debugLog("{$TAG} {uNet} {inference} Got latents: ${latents.hashCode()}")
        val shape = longArrayOf(2, 4, (height / 8).toLong(), (width / 8).toLong())
        debugLog("{$TAG} {uNet} {inference} Got shape: $shape")
        debugLog("{$TAG} {uNet} {inference} Starting steps processing! Total : ${timeSteps.size}")
        for (i in timeSteps.indices) {
            var latentModelInput: LocalDiffusionTensor<*> = duplicate(
                latents.tensor.floatBuffer.array(),
                shape,
            )
            latentModelInput = localDiffusionScheduler.scaleModelInput(latentModelInput, i)
            debugLog("{$TAG} {uNet} {inference} {Step_$i} ------------------")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Latent model input: $latentModelInput")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Notifying callback about step.")
            callback?.onStep(timeSteps.size, i)
            val input = createUNetModelInput(
                textEmbeddings,
                latentModelInput.tensor,
                OnnxTensor.createTensor(
                    ortEnvironmentProvider.get(),
                    IntBuffer.wrap(intArrayOf(timeSteps[i])),
                    longArrayOf(1)
                )
            )
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Got uNet model input: $input")
            val result = session!!.run(input)
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Got result from uNet session: $result")
            val dataSet = result[0].value as Array3D<FloatArray>
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Trying to close ORT session in: $result")
            result.close()
            val splitTensors: Pair<Array3D<FloatArray>, Array3D<FloatArray>> =
                splitTensor(
                    dataSet,
                    longArrayOf(1, 4, (height / 8).toLong(), (width / 8).toLong())
                )
            val noisePrediction = splitTensors.first
            val noisePredictionText = splitTensors.second
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Got split tensors with prediction:")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} - splitTensors: $splitTensors")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} - noisePrediction: $noisePrediction")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} - noisePredictionText: $noisePredictionText")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Trying to preform guidance...")
            performGuidance(noisePrediction, noisePredictionText, guidanceScale)
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Guidance performed successfully!")
            latents = localDiffusionScheduler.step(
                LocalDiffusionTensor(
                    OnnxTensor.createTensor(
                        ortEnvironmentProvider.get(),
                        noisePrediction,
                    ),
                    noisePrediction,
                    getSizes(noisePrediction),
                ),
                i,
                latents,
            )
            debugLog("{$TAG} {uNet} {inference} {Step_$i} Finalized latents: $latents")
            debugLog("{$TAG} {uNet} {inference} {Step_$i} ------------------")
        }
        callback?.also { clb ->
            debugLog("{$TAG} {uNet} {inference} Finalization / Flushing image...")
            callback?.onStep(timeSteps.size, timeSteps.size)
            val bitmap = decode(latents)
            debugLog("{$TAG} {uNet} {inference} Finalization / Decoded bitmap: ${bitmap.hashCode()}")
            clb.onBuildImage(0, bitmap)
            debugLog("{$TAG} {uNet} {inference} Finalization / Notifying callback and closing session.")
            close()
        }
    }

    fun decode(latents: LocalDiffusionTensor<*>): Bitmap {
        debugLog("{$TAG} {uNet} {decode} Trying to decode latents: ${latents.hashCode()}")
        val tensor: LocalDiffusionTensor<*> = multipleTensorsByFloat(
            latents.tensor.floatBuffer.array(),
            1.0f / 0.18215f,
            latents.shape
        )
        val decoderInput: MutableMap<String, OnnxTensor> = HashMap()
        decoderInput[KEY_LATENT_SAMPLE] = tensor.tensor
        val value: Any = decoder!!.decode(decoderInput.toMap())
        val bitmap = decoder!!.convertToImage(
            value as Array3D<FloatArray>,
            width,
            height,
        )
        debugLog("{$TAG} {uNet} {decode} Bitmap generated successfully: ${bitmap.hashCode()}")
        return bitmap
    }

    fun close() {
        debugLog("{$TAG} {uNet} {close} Closing session...")
        session?.close()
        decoder?.close()
        session = null
        decoder = null
        debugLog("{$TAG} {uNet} {close} Session closed successfully!")
    }

    fun setCallback(callback: Callback?) {
        debugLog("{$TAG} {uNet} Setting new result callback ${callback.hashCode()}")
        this.callback = callback
    }

    interface Callback {
        fun onStep(maxStep: Int, step: Int)
        fun onBuildImage(status: Int, bitmap: Bitmap?)
    }
}
