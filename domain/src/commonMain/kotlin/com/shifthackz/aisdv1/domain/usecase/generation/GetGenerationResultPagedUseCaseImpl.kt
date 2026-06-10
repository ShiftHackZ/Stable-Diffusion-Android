package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.repository.GenerationResultRepository

/**
 * Implements `GetGenerationResultPagedUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class GetGenerationResultPagedUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: GenerationResultRepository,
) : GetGenerationResultPagedUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @author Dmitriy Moroz
     */
    override suspend operator fun invoke(limit: Int, offset: Int) = repository.getPage(limit, offset)

    /**
     * Loads SDAI data through `observe`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun observe(limit: Int, offset: Int) = repository.observePage(limit, offset)

    /**
     * Loads SDAI data through `observeCount`.
     *
     * @author Dmitriy Moroz
     */
    override fun observeCount() = repository.observeCount()
}
