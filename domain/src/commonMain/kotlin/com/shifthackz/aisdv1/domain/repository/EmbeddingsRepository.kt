package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.Embedding

/**
 * Defines the `EmbeddingsRepository` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface EmbeddingsRepository {
    /**
     * Loads SDAI data through `fetchEmbeddings`.
     *
     * @author Dmitriy Moroz
     */
    suspend fun fetchEmbeddings()
    /**
     * Loads SDAI data through `fetchAndGetEmbeddings`.
     *
     * @return Result produced by `fetchAndGetEmbeddings`.
     * @author Dmitriy Moroz
     */
    suspend fun fetchAndGetEmbeddings(): List<Embedding>
    /**
     * Loads SDAI data through `getEmbeddings`.
     *
     * @return Result produced by `getEmbeddings`.
     * @author Dmitriy Moroz
     */
    suspend fun getEmbeddings(): List<Embedding>
}
