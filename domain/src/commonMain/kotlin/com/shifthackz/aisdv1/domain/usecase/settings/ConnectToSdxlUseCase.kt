package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToSdxlUseCase {
    suspend operator fun invoke(modelId: String, modelPath: String): Result<Unit>
}
