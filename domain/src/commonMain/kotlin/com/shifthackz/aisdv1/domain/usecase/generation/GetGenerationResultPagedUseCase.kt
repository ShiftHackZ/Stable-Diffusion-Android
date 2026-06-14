package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.AiGenerationResultPreview
import kotlinx.coroutines.flow.Flow

/**
 * Defines the `GetGenerationResultPagedUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetGenerationResultPagedUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(limit: Int, offset: Int): List<AiGenerationResult>
    /**
     * Loads SDAI data through `observe`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observe`.
     * @author Dmitriy Moroz
     */
    fun observe(limit: Int, offset: Int): Flow<List<AiGenerationResult>>
    /**
     * Loads SDAI data through `observePreview`.
     *
     * @param limit limit value consumed by the API.
     * @param offset offset value consumed by the API.
     * @return Result produced by `observePreview`.
     * @author Dmitriy Moroz
     */
    fun observePreview(limit: Int, offset: Int): Flow<List<AiGenerationResultPreview>>
    /**
     * Loads SDAI data through `observeCount`.
     *
     * @return Result produced by `observeCount`.
     * @author Dmitriy Moroz
     */
    fun observeCount(): Flow<Int>
}
