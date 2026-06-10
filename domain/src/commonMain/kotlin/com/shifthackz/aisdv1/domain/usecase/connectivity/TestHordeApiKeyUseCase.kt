package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestHordeApiKeyUseCase {
    suspend operator fun invoke(): Boolean
}
