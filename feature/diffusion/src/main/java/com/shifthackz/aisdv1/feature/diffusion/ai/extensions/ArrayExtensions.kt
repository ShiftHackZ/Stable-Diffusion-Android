package com.shifthackz.aisdv1.feature.diffusion.ai.extensions

import kotlin.math.ceil
import java.util.function.Function

internal fun arrange(start: Double, stop: Double, step: Double?): DoubleArray {
    val size = (step?.let { ceil((stop - start) / step) } ?: ceil(stop - start)).toInt()
    val result = DoubleArray(size)
    for (i in 0 until size) {
        result[i] = if (step != null) start + i * step else start + i
    }
    return result
}

internal fun lineSpace(start: Double, end: Double, steps: Int): DoubleArray {
    val doubles = DoubleArray(steps)
    for (i in 0 until steps) {
        doubles[i] = start + (end - start) * i / (steps - 1)
    }
    return doubles
}

internal fun interpolate(x: DoubleArray, xp: DoubleArray, fp: DoubleArray): DoubleArray {
    val y = DoubleArray(x.size)
    val interpolation = createLinearInterpolationFunction(xp, fp)
    for (i in y.indices) y[i] = interpolation.apply(x[i])
    return y
}

internal fun getSizes(dataSet: Array<Array<Array<FloatArray>>>): LongArray = longArrayOf(
    dataSet.size.toLong(),
    dataSet[0].size.toLong(),
    dataSet[0][0].size.toLong(),
    dataSet[0][0][0].size.toLong()
)

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
