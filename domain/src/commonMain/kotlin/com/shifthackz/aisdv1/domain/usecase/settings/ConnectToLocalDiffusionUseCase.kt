package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToLocalDiffusionUseCase {
    suspend operator fun invoke(
        modelId: String,
        modelPath: String,
    ): Result<Unit>
}
