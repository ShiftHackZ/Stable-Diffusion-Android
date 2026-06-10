package com.shifthackz.aisdv1.domain.usecase.debug

/**
 * Defines the `DebugInsertBadBase64UseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface DebugInsertBadBase64UseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}
