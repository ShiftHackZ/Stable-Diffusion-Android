package com.shifthackz.aisdv1.core.localization.formatter

import kotlin.math.abs

/**
 * Provides the `DurationFormatter` singleton used by the SDAI localization layer.
 *
 * @author Dmitriy Moroz
 */
object DurationFormatter {

    /**
     * Executes the `formatDurationInSeconds` step in the SDAI localization layer.
     *
     * @param seconds seconds value consumed by the API.
     * @return Result produced by `formatDurationInSeconds`.
     * @author Dmitriy Moroz
     */
    fun formatDurationInSeconds(seconds: Int): String {
        val absSeconds = abs(seconds.toLong())
        val hours = absSeconds / 3600
        val minutes = (absSeconds % 3600) / 60
        val remainingSeconds = absSeconds % 60
        val sign = if (seconds < 0) "-" else ""
        return "$sign$hours:${minutes.toTimePart()}:${remainingSeconds.toTimePart()}"
    }

    /**
     * Converts SDAI data with `toTimePart`.
     *
     * @author Dmitriy Moroz
     */
    private fun Long.toTimePart(): String = if (this < 10) "0$this" else "$this"
}
