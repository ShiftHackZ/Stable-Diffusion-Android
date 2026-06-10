package com.shifthackz.aisdv1.domain.repository

/**
 * Defines the `RandomImageRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface RandomImageRepository {
    /**
     * Loads SDAI data through `fetchAndGet`.
     *
     * @return Result produced by `fetchAndGet`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGet(): ByteArray
}
