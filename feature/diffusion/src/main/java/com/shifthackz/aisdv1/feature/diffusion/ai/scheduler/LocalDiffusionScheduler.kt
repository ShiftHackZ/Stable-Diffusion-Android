package com.shifthackz.aisdv1.feature.diffusion.ai.scheduler

import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionTensor

internal interface LocalDiffusionScheduler {
    val initNoiseSigma: Double
    fun setTimeSteps(numInferenceSteps: Int): IntArray
    fun scaleModelInput(sample: LocalDiffusionTensor<*>, stepIndex: Int): LocalDiffusionTensor<*>
    fun step(modelOutput: LocalDiffusionTensor<*>, stepIndex: Int, sample: LocalDiffusionTensor<*>): LocalDiffusionTensor<*>
}
