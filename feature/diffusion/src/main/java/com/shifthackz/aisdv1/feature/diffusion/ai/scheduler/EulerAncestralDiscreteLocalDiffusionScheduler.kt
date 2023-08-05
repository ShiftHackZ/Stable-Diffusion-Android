@file:Suppress("UNCHECKED_CAST")

package com.shifthackz.aisdv1.feature.diffusion.ai.scheduler

import ai.onnxruntime.OnnxTensor
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.arrange
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.interpolate
import com.shifthackz.aisdv1.feature.diffusion.ai.extensions.lineSpace
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionTensor
import com.shifthackz.aisdv1.feature.diffusion.environment.OrtEnvironmentProvider
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionConfig
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.nio.FloatBuffer
import java.util.Random
import kotlin.math.cos
import kotlin.math.pow

internal class EulerAncestralDiscreteLocalDiffusionScheduler(
    private val config: LocalDiffusionConfig = LocalDiffusionConfig(),
) : LocalDiffusionScheduler, KoinComponent {
    
    private val ortEnvironmentProvider: OrtEnvironmentProvider by inject()
    
    private val random = Random()
    private val betas: MutableList<Float> = ArrayList()
    private val alphas: MutableList<Float> = ArrayList()
    private val alphasCumpRod: MutableList<Float> = ArrayList()
    private val timeSteps: MutableList<Int> = ArrayList()
    private val sigmas: MutableList<Float> = ArrayList()
    override var initNoiseSigma = 1.0
    private var numInferenceSteps = 0
    private val numTrainTimeSteps = 1000
    private var isScaleInputCalled = false

    init {
        when {
            config.trainedBetas.isNotEmpty() -> {
                betas.addAll(config.trainedBetas)
            }
            config.betaSchedule == "linear" -> {
                val array: DoubleArray = lineSpace(
                    config.betaStart.toDouble(),
                    config.betaEnd.toDouble(),
                    numTrainTimeSteps,
                )
                for (value in array) {
                    betas.add(value.toFloat())
                }
            }
            config.betaSchedule == "scaled_linear" -> {
                val array: DoubleArray = lineSpace(
                    config.betaStart.toDouble().pow(0.5),
                    config.betaEnd.toDouble().pow(0.5),
                    numTrainTimeSteps
                )
                for (i in array.indices) {
                    betas.add(array[i].pow(2.0).toFloat())
                }
            }
            config.betaSchedule == "squaredcos_cap_v2" -> {
                betas.addAll(betasForAlphaBar(numTrainTimeSteps))
            }
        }

        for (value in betas) {
            alphas.add(1f - value)
        }
        for (i in alphas.indices) {
            val sub: List<Float> = alphas.subList(0, i + 1)
            var value = sub[0]
            for (x in 1 until sub.size) {
                value *= sub[x]
            }
            alphasCumpRod.add(value)
        }
        val sigmas: MutableList<Float> = ArrayList(alphasCumpRod.size)
        for (value in alphasCumpRod) {
            sigmas.add(((1f - value) / value).toDouble().pow(0.5).toFloat())
        }
        for (i in sigmas.indices.reversed()) {
            this.sigmas.add(sigmas[i])
        }
        this.sigmas.add(0f)
        var maxSigmas = this.sigmas[0]
        for (value in this.sigmas) if (value > maxSigmas) maxSigmas = value
        initNoiseSigma = maxSigmas.toDouble()
        val array: DoubleArray = lineSpace(
            0.0,
            (numTrainTimeSteps - 1).toDouble(),
            numTrainTimeSteps,
        )
        for (value in array) {
            timeSteps.add(0, value.toInt())
        }
    }

    override fun setTimeSteps(numInferenceSteps: Int): IntArray {
        this.numInferenceSteps = numInferenceSteps
        val array: DoubleArray = lineSpace(
            0.0,
            (numTrainTimeSteps - 1).toDouble(),
            numInferenceSteps,
        )
        val timeSteps = DoubleArray(array.size)
        for (i in 1..array.size) timeSteps[array.size - i] = array[i - 1]
        var sigmas = DoubleArray(alphasCumpRod.size)
        for (i in sigmas.indices) {
            val value = alphasCumpRod[i]
            sigmas[i] = ((1 - value) / value).toDouble().pow(0.5).toFloat().toDouble()
        }
        val arrange: DoubleArray = arrange(0.0, sigmas.size.toDouble(), null)
        sigmas = interpolate(timeSteps, arrange, sigmas)
        val extend = DoubleArray(sigmas.size + 1)
        System.arraycopy(sigmas, 0, extend, 0, sigmas.size)
        this.sigmas.clear()
        this.timeSteps.clear()
        for (value in extend) this.sigmas.add(value.toFloat())
        val result = IntArray(timeSteps.size)
        for (i in timeSteps.indices) {
            result[i] = timeSteps[i].toInt()
            this.timeSteps.add(timeSteps[i].toInt())
        }
        return result
    }

    override fun scaleModelInput(sample: LocalDiffusionTensor<*>, stepIndex: Int): LocalDiffusionTensor<*> {
        val sampleArray: FloatArray = sample.tensor.floatBuffer.array()
        val sigma = sigmas[stepIndex]
        val dataSet = FloatArray(sampleArray.size)
        for (i in dataSet.indices) {
            dataSet[i] = (sampleArray[i] / (sigma.toDouble().pow(2.0) + 1).pow(0.5)).toFloat()
        }
        isScaleInputCalled = true
        return LocalDiffusionTensor(
            OnnxTensor.createTensor(
                ortEnvironmentProvider.get(),
                FloatBuffer.wrap(dataSet),
                sample.shape
            ),
            null,
            sample.shape
        )
    }

    override fun step(modelOutput: LocalDiffusionTensor<*>, stepIndex: Int, sample: LocalDiffusionTensor<*>): LocalDiffusionTensor<*> {
        val sampleArray = sample.buffer as Array<Array<Array<FloatArray>>>
        val outputArray = modelOutput.buffer as Array<Array<Array<FloatArray>>>
        val sigma = sigmas[stepIndex].toDouble()
        val dim1 = modelOutput.shape!![0].toInt()
        val dim2 = modelOutput.shape!![1].toInt()
        val dim3 = modelOutput.shape!![2].toInt()
        val dim4 = modelOutput.shape!![3].toInt()
        var predictionOriginalSample: Array<Array<Array<FloatArray>>>? = null
        when (config.predictionType) {
            "epsilon" -> {
                predictionOriginalSample = Array(dim1.toInt()) {
                    Array(dim2.toInt()) {
                        Array(dim3.toInt()) {
                            FloatArray(dim4.toInt())
                        }
                    }
                }
                for (i in 0 until dim1) {
                    for (j in 0 until dim2) {
                        for (k in 0 until dim3) {
                            for (l in 0 until dim4) {
                                predictionOriginalSample[i][j][k][l] =
                                    (sampleArray[i][j][k][l] - sigma * outputArray[i][j][k][l]).toFloat()
                            }
                        }
                    }
                }
            }
            "v_prediction" -> {
                predictionOriginalSample = Array(dim1) {
                    Array(dim2) {
                        Array(dim3) {
                            FloatArray(dim4)
                        }
                    }
                }
                for (i in 0 until dim1) {
                    for (j in 0 until dim2) {
                        for (k in 0 until dim3) {
                            for (l in 0 until dim4) {
                                predictionOriginalSample[i][j][k][l] = (outputArray[i][j][k][l] * Math.pow(
                                    -sigma / (Math.pow(
                                        sigma,
                                        2.0
                                    ) + 1), 0.5
                                ) + sampleArray[i][j][k][l] / (Math.pow(sigma, 2.0) + 1)).toFloat()
                            }
                        }
                    }
                }
            }
        }
        val sigmaFrom = sigmas[stepIndex].toDouble()
        val sigmaTo = sigmas[stepIndex + 1].toDouble()
        val sigmaUp = (sigmaTo.pow(2.0) * (sigmaFrom.pow(2.0) - sigmaTo.pow(2.0)) / sigmaFrom.pow(
            2.0
        )).pow(0.5)
        val sigmaDown = (sigmaTo.pow(2.0) - sigmaUp.pow(2.0)).pow(0.5)
        val derivative = Array(dim1) {
            Array(dim2) {
                Array(dim3) {
                    FloatArray(dim4)
                }
            }
        }
        for (i in 0 until dim1) {
            for (j in 0 until dim2) {
                for (k in 0 until dim3) {
                    for (l in 0 until dim4) {
                        derivative[i][j][k][l] =
                            ((sampleArray[i][j][k][l] - predictionOriginalSample!![i][j][k][l]) / sigma).toFloat()
                    }
                }
            }
        }
        val dt = sigmaDown - sigma
        val prevSample = Array(dim1) {
            Array(dim2) {
                Array(dim3) {
                    FloatArray(dim4)
                }
            }
        }
        for (i in 0 until dim1) {
            for (j in 0 until dim2) {
                for (k in 0 until dim3) {
                    for (l in 0 until dim4) {
                        prevSample[i][j][k][l] =
                            (sampleArray[i][j][k][l] + derivative[i][j][k][l] * dt).toFloat()
                    }
                }
            }
        }

        /*float[][][][] noise = new float[dim1][dim2][dim3][dim4];
        Random random = new Random();
        for (int i = 0; i < dim1; i++){
            for (int j = 0; j < dim2; j++){
                for (int k = 0; k < dim3; k++){
                    for (int l = 0; l < dim4; l++){
                        noise[i][j][k][l] = (float) random.nextGaussian();
                    }
                }
            }
        }*/
        for (i in 0 until dim1) {
            for (j in 0 until dim2) {
                for (k in 0 until dim3) {
                    for (l in 0 until dim4) {
                        prevSample[i][j][k][l] =
                            (prevSample[i][j][k][l] + random.nextGaussian() * sigmaUp).toFloat()
                    }
                }
            }
        }
        return LocalDiffusionTensor(
            OnnxTensor.createTensor(ortEnvironmentProvider.get(), prevSample),
            prevSample,
            modelOutput.shape
        )
    }

    private fun betasForAlphaBar(
        numDiffusionTimeSteps: Int = 1000,
        maxBeta: Float = 0.999f,
    ): List<Float> {
        val betas: MutableList<Float> = ArrayList(numDiffusionTimeSteps)
        for (i in 0 until numDiffusionTimeSteps) {
            val t1 = i * 1.0 / numDiffusionTimeSteps
            val t2 = (i + 1) * 1.0 / numDiffusionTimeSteps
            betas.add((1 - alphaBar(t2) / alphaBar(t1)).coerceAtMost(maxBeta.toDouble()).toFloat())
        }
        return betas
    }

    private fun alphaBar(timeStep: Double): Double =
        cos((timeStep + 0.008) / 1.008 * Math.PI / 2).pow(2.0)

    companion object {
        const val TAG = "EulerAncestralDiscreteScheduler"
    }
}
