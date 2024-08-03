package com.shifthackz.aisdv1.core.common.time

import java.util.Date

interface TimeProvider {
    fun nanoTime(): Long
    fun currentTimeMillis(): Long
    fun currentDate(): Date
}
