package com.shifthackz.aisdv1.domain.usecase.connectivity

/**
 * Defines the `TestArliAiApiKeyUseCase` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface TestArliAiApiKeyUseCase {
    suspend operator fun invoke(): Boolean
}
