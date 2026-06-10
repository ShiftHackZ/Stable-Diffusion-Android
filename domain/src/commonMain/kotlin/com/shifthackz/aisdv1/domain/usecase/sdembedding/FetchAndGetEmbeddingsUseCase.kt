package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.Embedding

/**
 * Defines the `FetchAndGetEmbeddingsUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface FetchAndGetEmbeddingsUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): List<Embedding>
}
