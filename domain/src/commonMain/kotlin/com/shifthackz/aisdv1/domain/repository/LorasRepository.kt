package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.LoRA

/**
 * Defines the `LorasRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface LorasRepository {
    /**
     * Loads SDAI data through `fetchLoras`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchLoras()
    /**
     * Loads SDAI data through `fetchAndGetLoras`.
     *
     * @return Result produced by `fetchAndGetLoras`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetLoras(): List<LoRA>
    /**
     * Loads SDAI data through `getLoras`.
     *
     * @return Result produced by `getLoras`.
     * @author Dmitriy Moroz
     */
    suspend fun getLoras(): List<LoRA>
}
