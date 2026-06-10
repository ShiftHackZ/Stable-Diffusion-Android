package com.shifthackz.aisdv1.domain.usecase.generation

import com.shifthackz.aisdv1.domain.entity.AiGenerationResult
import com.shifthackz.aisdv1.domain.entity.TextToImagePayload

/**
 * Defines the `TextToImageUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface TextToImageUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param payload generation payload used by the operation.
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(payload: TextToImagePayload): List<AiGenerationResult>
}
