package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.RandomImageRepository

/**
 * Implements `GetRandomImageUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class GetRandomImageUseCaseImpl(
    /**
     * Exposes the `randomImageRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val randomImageRepository: RandomImageRepository,
) : GetRandomImageUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke() = randomImageRepository.fetchAndGet()
}
