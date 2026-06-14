package com.shifthackz.aisdv1.presentation.screen.benchmark

import platform.Foundation.NSDate
import platform.Foundation.NSDateFormatter
import platform.Foundation.NSDateFormatterMediumStyle
import platform.Foundation.NSDateFormatterShortStyle
import platform.Foundation.dateWithTimeIntervalSince1970

internal actual fun formatBenchmarkTimestamp(epochMillis: Long): String {
    val formatter = NSDateFormatter().apply {
        dateStyle = NSDateFormatterMediumStyle
        timeStyle = NSDateFormatterShortStyle
    }
    return formatter.stringFromDate(
        NSDate.dateWithTimeIntervalSince1970(epochMillis / 1_000.0),
    )
}
