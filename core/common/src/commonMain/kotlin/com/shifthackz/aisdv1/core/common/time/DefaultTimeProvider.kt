package com.shifthackz.aisdv1.core.common.time

/**
 * Provides the `DefaultTimeProvider` singleton used by the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
object DefaultTimeProvider : TimeProvider {
    /**
     * Executes the `nanoTime` step in the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override fun nanoTime(): Long = platformNanoTime()
    /**
     * Executes the `currentTimeMillis` step in the SDAI core common layer.
     *
     * @author Dmitriy Moroz
     */
    override fun currentTimeMillis(): Long = platformCurrentTimeMillis()
}

/**
 * Executes the `platformNanoTime` step in the SDAI core common layer.
 *
 * @return Result produced by `platformNanoTime`.
 * @author Dmitriy Moroz
 */
internal expect fun platformNanoTime(): Long

/**
 * Executes the `platformCurrentTimeMillis` step in the SDAI core common layer.
 *
 * @return Result produced by `platformCurrentTimeMillis`.
 * @author Dmitriy Moroz
 */
internal expect fun platformCurrentTimeMillis(): Long
