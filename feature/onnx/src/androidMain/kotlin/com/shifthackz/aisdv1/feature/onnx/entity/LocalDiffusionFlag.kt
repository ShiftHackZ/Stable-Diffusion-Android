@file:Suppress("unused")

package com.shifthackz.aisdv1.feature.onnx.entity

/**
 * Coordinates `LocalDiffusionFlag` behavior in the SDAI ONNX local diffusion feature layer.
 *
 * @param value value value consumed by the API.
 * @author Dmitriy Moroz
 */
enum class LocalDiffusionFlag(val value: Int) {
    CPU(0),
    NN_API(1);
}
