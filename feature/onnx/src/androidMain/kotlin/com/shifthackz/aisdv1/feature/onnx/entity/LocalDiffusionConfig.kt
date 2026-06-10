package com.shifthackz.aisdv1.feature.onnx.entity

import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.BETA_SCHEDULER_SCALED_LINEAR
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.DPM_SOLVER_PP
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.PREDICTION_EPSILON
import com.shifthackz.aisdv1.feature.onnx.LocalDiffusionContract.SOLVER_MIDPOINT

/**
 * Carries `LocalDiffusionConfig` data through the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal data class LocalDiffusionConfig(
    /**
     * Exposes the `betaStart` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val betaStart: Float = 0.00085f,
    /**
     * Exposes the `betaEnd` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val betaEnd: Float = 0.012f,
    /**
     * Exposes the `betaSchedule` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val betaSchedule: String = BETA_SCHEDULER_SCALED_LINEAR,
    /**
     * Exposes the `trainedBetas` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val trainedBetas: List<Float> = emptyList(),
    /**
     * Exposes the `solverOrder` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val solverOrder: Int = 2,
    /**
     * Exposes the `predictionType` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val predictionType: String = PREDICTION_EPSILON,
    /**
     * Exposes the `thresholding` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val thresholding: Boolean = false,
    /**
     * Exposes the `dynamicThresholdingRatio` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val dynamicThresholdingRatio: Float = 0.995f,
    /**
     * Exposes the `sampleMaxValue` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val sampleMaxValue: Float = 1.0f,
    /**
     * Exposes the `algorithmType` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val algorithmType: String = DPM_SOLVER_PP,
    /**
     * Exposes the `solverType` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val solverType: String = SOLVER_MIDPOINT,
    /**
     * Exposes the `lowerOrderFinal` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val lowerOrderFinal: Boolean = true,
    /**
     * Exposes the `clipSample` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val clipSample: Boolean = false,
    /**
     * Exposes the `clipSampleRange` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val clipSampleRange: Float = 1.0f,
)
