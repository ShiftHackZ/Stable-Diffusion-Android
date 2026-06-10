package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToStabilityAiUseCase {
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
