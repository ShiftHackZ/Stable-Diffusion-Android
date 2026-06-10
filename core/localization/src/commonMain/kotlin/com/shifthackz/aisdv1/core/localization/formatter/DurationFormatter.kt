package com.shifthackz.aisdv1.core.localization.formatter

import kotlin.math.abs

object DurationFormatter {

    fun formatDurationInSeconds(seconds: Int): String {
        val absSeconds = abs(seconds.toLong())
        val hours = absSeconds / 3600
        val minutes = (absSeconds % 3600) / 60
        val remainingSeconds = absSeconds % 60
        val sign = if (seconds < 0) "-" else ""
        return "$sign$hours:${minutes.toTimePart()}:${remainingSeconds.toTimePart()}"
    }

    private fun Long.toTimePart(): String = if (this < 10) "0$this" else "$this"
}
