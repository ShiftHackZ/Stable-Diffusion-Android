package com.shifthackz.aisdv1.domain.usecase.sdembedding

import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository

/**
 * Implements `FetchAndGetEmbeddingsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchAndGetEmbeddingsUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: EmbeddingsRepository,
) : FetchAndGetEmbeddingsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): List<Embedding> = repository.fetchAndGetEmbeddings()
}
