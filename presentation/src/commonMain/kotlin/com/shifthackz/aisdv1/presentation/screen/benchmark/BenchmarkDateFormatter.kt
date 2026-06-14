package com.shifthackz.aisdv1.presentation.screen.benchmark

/**
 * Formats benchmark timestamps with the user's platform locale.
 *
 * @param epochMillis timestamp in milliseconds since Unix epoch.
 * @return localized timestamp.
 * @author Dmitriy Moroz
 */
internal expect fun formatBenchmarkTimestamp(epochMillis: Long): String
