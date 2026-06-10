package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestStabilityAiApiKeyUseCase {
    suspend operator fun invoke(): Boolean
}
