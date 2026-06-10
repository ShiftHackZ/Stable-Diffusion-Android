package com.shifthackz.aisdv1.feature.onnx.entity

import ai.onnxruntime.OnnxTensor

/**
 * Carries `LocalDiffusionTensor` data through the SDAI ONNX local diffusion feature layer.
 *
 * @author Dmitriy Moroz
 */
internal data class LocalDiffusionTensor<T>(
    /**
     * Exposes the `tensor` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    var tensor: OnnxTensor,
    /**
     * Exposes the `buffer` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    var buffer: T?,
    /**
     * Exposes the `shape` value used by the SDAI ONNX local diffusion feature layer.
     *
     * @author Dmitriy Moroz
     */
    var shape: LongArray?,
) {

    /**
     * Executes the `equals` step in the SDAI ONNX local diffusion feature layer.
     *
     * @param other other value consumed by the API.
     * @return Result produced by `equals`.
     * @author Dmitriy Moroz
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LocalDiffusionTensor<*>

        if (tensor != other.tensor) return false
        if (buffer != other.buffer) return false
        if (shape != null) {
            if (other.shape == null) return false
            if (!shape.contentEquals(other.shape)) return false
        } else if (other.shape != null) return false

        return true
    }

    /**
     * Executes the `hashCode` step in the SDAI ONNX local diffusion feature layer.
     *
     * @return Result produced by `hashCode`.
     * @author Dmitriy Moroz
     */
    override fun hashCode(): Int {
        var result = tensor.hashCode()
        result = 31 * result + (buffer?.hashCode() ?: 0)
        result = 31 * result + (shape?.contentHashCode() ?: 0)
        return result
    }
}
