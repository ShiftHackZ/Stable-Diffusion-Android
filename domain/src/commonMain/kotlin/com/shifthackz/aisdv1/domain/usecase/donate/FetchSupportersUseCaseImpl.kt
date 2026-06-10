package com.shifthackz.aisdv1.domain.usecase.donate

import com.shifthackz.aisdv1.domain.entity.Supporter
import com.shifthackz.aisdv1.domain.repository.SupportersRepository

/**
 * Implements `FetchSupportersUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchSupportersUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: SupportersRepository,
) : FetchSupportersUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): List<Supporter> = repository.fetchAndGetSupporters()
}
