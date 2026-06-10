package com.shifthackz.aisdv1.domain.usecase.generation

/**
 * Defines the `GetRandomImageUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface GetRandomImageUseCase {
    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke(): ByteArray
}
