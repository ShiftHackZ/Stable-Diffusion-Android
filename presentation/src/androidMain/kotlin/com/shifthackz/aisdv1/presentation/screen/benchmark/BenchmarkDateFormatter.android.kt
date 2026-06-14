package com.shifthackz.aisdv1.presentation.screen.benchmark

import java.text.DateFormat
import java.util.Date

internal actual fun formatBenchmarkTimestamp(epochMillis: Long): String =
    DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)
        .format(Date(epochMillis))
