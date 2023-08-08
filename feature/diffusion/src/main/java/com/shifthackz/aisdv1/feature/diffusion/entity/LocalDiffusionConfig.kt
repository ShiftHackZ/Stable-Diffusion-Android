package com.shifthackz.aisdv1.feature.diffusion.entity

import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.BETA_SCHEDULER_SCALED_LINEAR
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.DPM_SOLVER_PP
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.PREDICTION_EPSILON
import com.shifthackz.aisdv1.feature.diffusion.LocalDiffusionContract.SOLVER_MIDPOINT

internal data class LocalDiffusionConfig(
    val betaStart: Float = 0.00085f,
    val betaEnd: Float = 0.012f,
    val betaSchedule: String = BETA_SCHEDULER_SCALED_LINEAR,
    val trainedBetas: List<Float> = emptyList(),
    val solverOrder: Int = 2,
    val predictionType: String = PREDICTION_EPSILON,
    val thresholding: Boolean = false,
    val dynamicThresholdingRatio: Float = 0.995f,
    val sampleMaxValue: Float = 1.0f,
    val algorithmType: String = DPM_SOLVER_PP,
    val solverType: String = SOLVER_MIDPOINT,
    val lowerOrderFinal: Boolean = true,
    val clipSample: Boolean = false,
    val clipSampleRange: Float = 1.0f,
)
