package com.shifthackz.aisdv1.feature.onnx.ai.scheduler

import com.shifthackz.aisdv1.feature.onnx.entity.LocalDiffusionTensor

/**
 * Defines the `LocalDiffusionScheduler` contract for the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal interface LocalDiffusionScheduler {
    /**
     * Exposes the `initNoiseSigma` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    val initNoiseSigma: Double
    /**
     * Executes the `setTimeSteps` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param numInferenceSteps num inference steps value consumed by the API.
     * @return Result produced by `setTimeSteps`.
     * @author Dmitriy Moroz
     */
    fun setTimeSteps(numInferenceSteps: Int): IntArray
    /**
     * Executes the `scaleModelInput` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param sample sample value consumed by the API.
     * @param stepIndex step index value consumed by the API.
     * @return Result produced by `scaleModelInput`.
     * @author Dmitriy Moroz
     */
    fun scaleModelInput(sample: LocalDiffusionTensor<*>, stepIndex: Int): LocalDiffusionTensor<*>
    /**
     * Executes the `step` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param modelOutput model output value consumed by the API.
     * @param stepIndex step index value consumed by the API.
     * @param sample sample value consumed by the API.
     * @return Result produced by `step`.
     * @author Dmitriy Moroz
     */
    fun step(modelOutput: LocalDiffusionTensor<*>, stepIndex: Int, sample: LocalDiffusionTensor<*>): LocalDiffusionTensor<*>
}
