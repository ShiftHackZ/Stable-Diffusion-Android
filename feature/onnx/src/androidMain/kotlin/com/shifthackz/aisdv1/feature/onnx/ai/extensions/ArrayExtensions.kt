package com.shifthackz.aisdv1.feature.onnx.ai.extensions

import com.shifthackz.aisdv1.feature.onnx.entity.Array3D
import java.util.function.Function
import kotlin.math.ceil

/**
 * Executes the `arrange` step in the SDAI ONNX local diffusion feature layer.
 *
 * @param start start value consumed by the API.
 * @param stop stop value consumed by the API.
 * @param step step value consumed by the API.
 * @return Result produced by `arrange`.
 * @author Dmitriy Moroz
 */
internal fun arrange(start: Double, stop: Double, step: Double?): DoubleArray {
    val size = (step?.let { ceil((stop - start) / step) } ?: ceil(stop - start)).toInt()
    val result = DoubleArray(size)
    for (i in 0 until size) {
        result[i] = if (step != null) start + i * step else start + i
    }
    return result
}

/**
 * Executes the `lineSpace` step in the SDAI ONNX local diffusion feature layer.
 *
 * @param start start value consumed by the API.
 * @param end end value consumed by the API.
 * @param steps steps value consumed by the API.
 * @return Result produced by `lineSpace`.
 * @author Dmitriy Moroz
 */
internal fun lineSpace(start: Double, end: Double, steps: Int): DoubleArray {
    val doubles = DoubleArray(steps)
    for (i in 0 until steps) {
        doubles[i] = start + (end - start) * i / (steps - 1)
    }
    return doubles
}

/**
 * Executes the `interpolate` step in the SDAI ONNX local diffusion feature layer.
 *
 * @param x x value consumed by the API.
 * @param xp xp value consumed by the API.
 * @param fp fp value consumed by the API.
 * @return Result produced by `interpolate`.
 * @author Dmitriy Moroz
 */
internal fun interpolate(x: DoubleArray, xp: DoubleArray, fp: DoubleArray): DoubleArray {
    val y = DoubleArray(x.size)
    val interpolation = createLinearInterpolationFunction(xp, fp)
    for (i in y.indices) y[i] = interpolation.apply(x[i])
    return y
}

/**
 * Loads SDAI data through `getSizes`.
 *
 * @param dataSet data set value consumed by the API.
 * @author Dmitriy Moroz
 */
internal fun getSizes(dataSet: Array3D<FloatArray>): LongArray = longArrayOf(
    dataSet.size.toLong(),
    dataSet[0].size.toLong(),
    dataSet[0][0].size.toLong(),
    dataSet[0][0][0].size.toLong()
)

/**
 * Creates the SDAI value produced by `createLinearInterpolationFunction`.
 *
 * @param x x value consumed by the API.
 * @param y y value consumed by the API.
 * @return Result produced by `createLinearInterpolationFunction`.
 * @throws IllegalArgumentException when input validation fails.
 * @author Dmitriy Moroz
 */
internal fun createLinearInterpolationFunction(
    x: DoubleArray,
    y: DoubleArray,
): Function<Double, Double> {
    require(x.size == y.size) { "x and y must have the same length" }
    return Function { input: Double ->
        var i = 0
        while (i < x.size && x[i] < input) {
            i++
        }
        return@Function when (i) {
            0 -> y[0]
            x.size -> y[y.size - 1]
            else -> {
                val slope = (y[i] - y[i - 1]) / (x[i] - x[i - 1])
                y[i - 1] + slope * (input - x[i - 1])
            }
        }
    }
}
