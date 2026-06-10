package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToHuggingFaceUseCase {
    suspend operator fun invoke(apiKey: String, model: String): Result<Unit>
}
