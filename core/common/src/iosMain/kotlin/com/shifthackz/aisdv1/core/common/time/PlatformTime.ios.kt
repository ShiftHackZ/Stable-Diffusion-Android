@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.shifthackz.aisdv1.core.common.time

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

/**
 * Executes the `platformNanoTime` step in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
internal actual fun platformNanoTime(): Long = platformCurrentTimeMillis() * 1_000_000L

/**
 * Executes the `platformCurrentTimeMillis` step in the SDAI core common layer.
 *
 * @return Result produced by `platformCurrentTimeMillis`.
 * @author Dmitriy Moroz
 */
internal actual fun platformCurrentTimeMillis(): Long = memScoped {
    val time = alloc<timeval>()
    gettimeofday(time.ptr, null)
    time.tv_sec * 1_000L + time.tv_usec / 1_000L
}
