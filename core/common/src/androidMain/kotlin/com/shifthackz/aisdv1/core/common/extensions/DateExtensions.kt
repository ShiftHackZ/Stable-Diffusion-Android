package com.shifthackz.aisdv1.core.common.extensions

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Loads SDAI data through `getRawDay`.
 *
 * @author Dmitriy Moroz
 */
fun Date.getRawDay(): Int = SimpleDateFormat("dd", Locale.ROOT).format(this).toInt()

/**
 * Loads SDAI data through `getRawMonth`.
 *
 * @author Dmitriy Moroz
 */
fun Date.getRawMonth(): Int = SimpleDateFormat("MM", Locale.ROOT).format(this).toInt()

/**
 * Loads SDAI data through `getRawYear`.
 *
 * @author Dmitriy Moroz
 */
fun Date.getRawYear(): Int = SimpleDateFormat("yyyy", Locale.ROOT).format(this).toInt()

/**
 * Loads SDAI data through `getDayRange`.
 *
 * @return Result produced by `getDayRange`.
 * @author Dmitriy Moroz
 */
fun Date.getDayRange(): Pair<Date, Date> {
    val formatter = SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss.SSS", Locale.ROOT)
    val prefix = "${getRawDay()}.${getRawMonth()}.${getRawYear()}"
    val start = formatter.parse("${prefix}T00:00:00.000") ?: this
    val end = formatter.parse("${prefix}T23:59:59.999") ?: this
    return start to end
}

/**
 * Executes the `format` step in the SDAI core common layer.
 *
 * @param format format value consumed by the API.
 * @param locale locale value consumed by the API.
 * @return Result produced by `format`.
 * @author Dmitriy Moroz
 */
fun Date.format(
    format: String = "yyyy-MM-dd",
    locale: Locale = Locale.ROOT,
): String = runCatching {
    val df = SimpleDateFormat(format, locale)
    df.format(this)
}.getOrDefault("")

/**
 * Converts SDAI data with `toDate`.
 *
 * @param format format value consumed by the API.
 * @param locale locale value consumed by the API.
 * @return Result produced by `toDate`.
 * @author Dmitriy Moroz
 */
fun String.toDate(
    format: String = "yyyy-MM-dd",
    locale: Locale = Locale.ROOT,
): Date = runCatching {
    val df = SimpleDateFormat(format, locale)
    df.parse(this)
}.getOrDefault(Date())
