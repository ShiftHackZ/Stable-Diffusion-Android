package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToOpenAiUseCase {
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
