package com.shifthackz.aisdv1.feature.diffusion.entity

import ai.onnxruntime.OnnxTensor

internal data class LocalDiffusionTensor<T>(
    var tensor: OnnxTensor,
    var buffer: T?,
    var shape: LongArray?,
) {

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

    override fun hashCode(): Int {
        var result = tensor.hashCode()
        result = 31 * result + (buffer?.hashCode() ?: 0)
        result = 31 * result + (shape?.contentHashCode() ?: 0)
        return result
    }
}
