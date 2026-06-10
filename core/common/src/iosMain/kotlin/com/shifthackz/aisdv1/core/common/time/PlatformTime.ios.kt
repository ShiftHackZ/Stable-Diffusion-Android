@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.shifthackz.aisdv1.core.common.time

import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import platform.posix.gettimeofday
import platform.posix.timeval

internal actual fun platformNanoTime(): Long = platformCurrentTimeMillis() * 1_000_000L

internal actual fun platformCurrentTimeMillis(): Long = memScoped {
    val time = alloc<timeval>()
    gettimeofday(time.ptr, null)
    time.tv_sec * 1_000L + time.tv_usec / 1_000L
}
