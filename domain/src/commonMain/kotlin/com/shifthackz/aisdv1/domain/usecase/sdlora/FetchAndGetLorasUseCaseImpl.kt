package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.repository.LorasRepository

/**
 * Implements `FetchAndGetLorasUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchAndGetLorasUseCaseImpl(
    /**
     * Exposes the `lorasRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val lorasRepository: LorasRepository,
) : FetchAndGetLorasUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = lorasRepository.fetchAndGetLoras()
}
