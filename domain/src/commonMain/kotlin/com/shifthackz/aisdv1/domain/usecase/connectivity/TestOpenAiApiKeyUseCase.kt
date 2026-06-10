package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestOpenAiApiKeyUseCase {
    suspend operator fun invoke(): Boolean
}
