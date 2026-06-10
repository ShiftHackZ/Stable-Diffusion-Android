package com.shifthackz.aisdv1.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Defines the `StabilityAiCreditsRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface StabilityAiCreditsRepository {
    /**
     * Loads SDAI data through `fetch`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetch()
    /**
     * Loads SDAI data through `fetchAndGet`.
     *
     * @return Result produced by `fetchAndGet`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGet(): Float
    /**
     * Loads SDAI data through `fetchAndObserve`.
     *
     * @return Result produced by `fetchAndObserve`.
     * @author Dmitriy Moroz
     */
    fun fetchAndObserve(): Flow<Float>
    /**
     * Loads SDAI data through `get`.
     *
     * @return Result produced by `get`.
     * @author Dmitriy Moroz
     */
    suspend fun get(): Float
    /**
     * Loads SDAI data through `observe`.
     *
     * @return Result produced by `observe`.
     * @author Dmitriy Moroz
     */
    fun observe(): Flow<Float>
}
