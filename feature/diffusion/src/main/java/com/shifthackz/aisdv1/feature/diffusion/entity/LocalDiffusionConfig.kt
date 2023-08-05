package com.shifthackz.aisdv1.feature.diffusion.entity

internal data class LocalDiffusionConfig(
    val betaStart: Float = 0.00085f,
    val betaEnd: Float = 0.012f,
    val betaSchedule: String = "scaled_linear",
    val trainedBetas: List<Float> = emptyList(),
    val solverOrder: Int = 2,
    val predictionType: String = "epsilon",
    val thresholding: Boolean = false,
    val dynamicThresholdingRatio: Float = 0.995f,
    val sampleMaxValue: Float = 1.0f,
    val algorithmType: String = "dpmsolver++",
    val solverType: String = "midpoint",
    val lowerOrderFinal: Boolean = true,
    val clipSample: Boolean = false,
    val clipSampleRange: Float = 1.0f,
)
