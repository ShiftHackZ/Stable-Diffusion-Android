package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Supporter

/**
 * Defines the `SupportersRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface SupportersRepository {
    /**
     * Loads SDAI data through `fetchSupporters`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchSupporters()
    /**
     * Loads SDAI data through `fetchAndGetSupporters`.
     *
     * @return Result produced by `fetchAndGetSupporters`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetSupporters(): List<Supporter>
    /**
     * Loads SDAI data through `getSupporters`.
     *
     * @return Result produced by `getSupporters`.
     * @author Dmitriy Moroz
     */
    suspend fun getSupporters(): List<Supporter>
}
