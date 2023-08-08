@file:Suppress("UNCHECKED_CAST", "MemberVisibilityCanBePrivate")

package com.shifthackz.aisdv1.feature.diffusion.ai.unet

import ai.onnxruntime.OnnxTensor
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OrtSession.SessionOptions
import ai.onnxruntime.providers.NNAPIFlags
import android.content.Context
import android.graphics.Bitmap
import android.util.Pair
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.getSizes
import com.shifthackz.aisdv1.feature.diffusion.ai.scheduler.EulerAncestralDiscreteLocalDiffusionScheduler
import com.shifthackz.aisdv1.feature.diffusion.ai.scheduler.LocalDiffusionScheduler
import com.shifthackz.aisdv1.feature.diffusion.ai.vae.VaeDecoder
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionTensor
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.diffusion.utils.PathManager
import com.shifthackz.aisdv1.feature.diffusion.utils.TensorProcessor
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.io.File
import java.nio.IntBuffer
import java.util.EnumSet
import java.util.Random
import kotlin.math.cos
import kotlin.math.ln
import kotlin.math.sqrt

internal class UNet(
    private val context: Context,
    private val deviceId: Int = LocalDiffusionFlag.CPU.value,
) : KoinComponent {

    private val ortEnvironmentProvider: OrtEnvironmentProvider by inject()
    private val decoder: VaeDecoder = VaeDecoder(context, deviceId)
    private val model = "unet/model.ort"
    private val random = Random()

    private var callback: Callback? = null
    private var session: OrtSession? = null

    private var width = 384
    private var height = 384

    fun initialize() {
        if (session != null) return
        val options = SessionOptions()
        options.addConfigEntry("session.load_model_format", "ORT")
        if (deviceId == LocalDiffusionFlag.NN_API.value) {
            options.addNnapi(EnumSet.of(NNAPIFlags.CPU_DISABLED))
        }
        val file = File(PathManager.getCustomPath(context) + "/" + model)
        session = ortEnvironmentProvider.get().createSession(
            if (file.exists()) file.absolutePath else PathManager.getModelPath(
                context
            ) + "/" + model, options
        )
    }

    private fun createUnetModelInput(
        encoderHiddenStates: OnnxTensor,
        sample: OnnxTensor,
        timeStep: OnnxTensor
    ): Map<String, OnnxTensor> {
        val map: MutableMap<String, OnnxTensor> = HashMap()
        map["encoder_hidden_states"] = encoderHiddenStates
        map["sample"] = sample
        map["timestep"] = timeStep
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
        noisePrediction: Array<Array<Array<FloatArray>>>,
        noisePredictionText: Array<Array<Array<FloatArray>>>,
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
        this.width = width
        this.height = height
        val localDiffusionScheduler: LocalDiffusionScheduler =
            EulerAncestralDiscreteLocalDiffusionScheduler()
        val timesteps: IntArray = localDiffusionScheduler.setTimeSteps(numInferenceSteps)
        val seed = if (seedNum <= 0) random.nextLong() else seedNum
        var latents: LocalDiffusionTensor<*> = generateLatentSample(
            batchSize,
            height,
            width,
            seed,
            localDiffusionScheduler.initNoiseSigma.toFloat()
        )
        val shape = longArrayOf(2, 4, (height / 8).toLong(), (width / 8).toLong())
        for (i in timesteps.indices) {
            var latentModelInput: LocalDiffusionTensor<*> = TensorProcessor.duplicate(
                latents.tensor.floatBuffer.array(),
                shape,
            )
            latentModelInput = localDiffusionScheduler.scaleModelInput(latentModelInput, i)
            callback?.onStep(timesteps.size, i)
            val input = createUnetModelInput(
                textEmbeddings,
                latentModelInput.tensor,
                OnnxTensor.createTensor(
                    ortEnvironmentProvider.get(),
                    IntBuffer.wrap(intArrayOf(timesteps[i])),
                    longArrayOf(1)
                )
            )
            val result = session!!.run(input)
            val dataSet = result[0].value as Array<Array<Array<FloatArray>>>
            result.close()
            val splitTensors: Pair<Array<Array<Array<FloatArray>>>, Array<Array<Array<FloatArray>>>> =
                TensorProcessor.splitTensor(
                    dataSet,
                    longArrayOf(1, 4, (height / 8).toLong(), (width / 8).toLong())
                )
            val noisePrediction = splitTensors.first
            val noisePredictionText = splitTensors.second
            performGuidance(noisePrediction, noisePredictionText, guidanceScale)
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
        }
        close()
        callback?.also { clb ->
            val bitmap = decode(latents)
            clb.onBuildImage(0, bitmap)
        }
    }

    fun decode(latents: LocalDiffusionTensor<*>): Bitmap {
        val tensor: LocalDiffusionTensor<*> = TensorProcessor.multipleTensorsByFloat(
            latents.tensor.floatBuffer.array(),
            1.0f / 0.18215f,
            latents.shape
        )
        val decoderInput: MutableMap<String, OnnxTensor> = HashMap()
        decoderInput["latent_sample"] = tensor.tensor
        val value: Any = decoder.decode(decoderInput.toMap())
        return decoder.convertToImage(
            value as Array<Array<Array<FloatArray>>>,
            width,
            height,
        )
    }

    fun close() {
        session?.close()
        decoder.close()
        session = null
    }

    fun setCallback(callback: Callback?) {
        this.callback = callback
    }

    interface Callback {
        fun onStep(maxStep: Int, step: Int)
        fun onBuildImage(status: Int, bitmap: Bitmap?)
    }
}
