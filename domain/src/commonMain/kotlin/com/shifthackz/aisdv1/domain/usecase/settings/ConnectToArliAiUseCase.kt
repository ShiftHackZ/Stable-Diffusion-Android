package com.shifthackz.aisdv1.domain.usecase.settings

/**
 * Defines the `ConnectToArliAiUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface ConnectToArliAiUseCase {
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
