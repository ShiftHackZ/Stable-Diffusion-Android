package com.shifthackz.aisdv1.presentation.utils

import kotlin.math.abs

fun formatDuration(seconds: Int): String {
    val absSeconds = abs(seconds)
    val value = String.format(
        "%d:%02d:%02d",
        absSeconds / 3600,
        (absSeconds % 3600) / 60,
        absSeconds % 60
    )
    val sign = if (seconds < 0) "-" else ""
    return "$sign$value"
}
