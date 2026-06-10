package com.shifthackz.aisdv1.core.common.time

internal actual fun platformNanoTime(): Long = System.nanoTime()

internal actual fun platformCurrentTimeMillis(): Long = System.currentTimeMillis()
