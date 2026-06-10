package com.shifthackz.aisdv1.core.common.time

/**
 * Defines the `TimeProvider` contract for the SDAI core common layer.
 *
 * @author Dmitriy Moroz
 */
interface TimeProvider {
    /**
     * Executes the `nanoTime` step in the SDAI core common layer.
     *
     * @return Result produced by `nanoTime`.
     * @author Dmitriy Moroz
     */
    fun nanoTime(): Long
    /**
     * Executes the `currentTimeMillis` step in the SDAI core common layer.
     *
     * @return Result produced by `currentTimeMillis`.
     * @author Dmitriy Moroz
     */
    fun currentTimeMillis(): Long
}
