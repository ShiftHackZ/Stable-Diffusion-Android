package com.shifthackz.aisdv1.domain.usecase.settings

interface ConnectToHordeUseCase {
    suspend operator fun invoke(apiKey: String): Result<Unit>
}
