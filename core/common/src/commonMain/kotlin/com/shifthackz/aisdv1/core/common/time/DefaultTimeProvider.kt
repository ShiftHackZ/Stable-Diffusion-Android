package com.shifthackz.aisdv1.core.common.time

object DefaultTimeProvider : TimeProvider {
    override fun nanoTime(): Long = platformNanoTime()
    override fun currentTimeMillis(): Long = platformCurrentTimeMillis()
}

internal expect fun platformNanoTime(): Long

internal expect fun platformCurrentTimeMillis(): Long
