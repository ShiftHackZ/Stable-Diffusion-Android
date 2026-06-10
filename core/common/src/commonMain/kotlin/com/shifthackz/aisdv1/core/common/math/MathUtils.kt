package com.shifthackz.aisdv1.core.common.math

import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * Executes the `roundTo` step in the SDAI core common layer.
 *
 * @param numFractionDigits num fraction digits value consumed by the API.
 * @return Result produced by `roundTo`.
 * @author Dmitriy Moroz
 */
fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

/**
 * Executes the `roundTo` step in the SDAI core common layer.
 *
 * @param numFractionDigits num fraction digits value consumed by the API.
 * @return Result produced by `roundTo`.
 * @author Dmitriy Moroz
 */
fun Float.roundTo(numFractionDigits: Int): Float {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return ((this * factor).roundToInt() / factor).toFloat()
}
