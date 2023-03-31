package com.shifthackz.aisdv1.core.common.extensions

import java.text.SimpleDateFormat
import java.util.*

fun Date.getRawDay(): Int = SimpleDateFormat("dd", Locale.ROOT).format(this).toInt()

fun Date.getRawMonth(): Int = SimpleDateFormat("MM", Locale.ROOT).format(this).toInt()

fun Date.getRawYear(): Int = SimpleDateFormat("yyyy", Locale.ROOT).format(this).toInt()

fun Date.getDayRange(): Pair<Date, Date> {
    val formatter = SimpleDateFormat("dd.MM.yyyy'T'HH:mm:ss.SSS", Locale.ROOT)
    val prefix = "${getRawDay()}.${getRawMonth()}.${getRawYear()}"
    val start = formatter.parse("${prefix}T00:00:00.000") ?: this
    val end = formatter.parse("${prefix}T23:59:59.999") ?: this
    return start to end
}
