package com.shifthackz.aisdv1.core.common.time

/**
 * Executes the `platformNanoTime` step in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
internal actual fun platformNanoTime(): Long = System.nanoTime()

/**
 * Executes the `platformCurrentTimeMillis` step in the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
internal actual fun platformCurrentTimeMillis(): Long = System.currentTimeMillis()
