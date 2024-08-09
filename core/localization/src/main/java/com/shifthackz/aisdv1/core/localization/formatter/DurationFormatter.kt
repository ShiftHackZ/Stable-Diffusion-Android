package com.shifthackz.aisdv1.core.localization.formatter

import java.util.Locale
import kotlin.math.abs

object DurationFormatter {

    fun formatDurationInSeconds(seconds: Int): String {
        val absSeconds = abs(seconds)
        val value = String.format(
            Locale.ROOT,
            "%d:%02d:%02d",
            absSeconds / 3600,
            (absSeconds % 3600) / 60,
            absSeconds % 60
        )
        val sign = if (seconds < 0) "-" else ""
        return "$sign$value"
    }
}
