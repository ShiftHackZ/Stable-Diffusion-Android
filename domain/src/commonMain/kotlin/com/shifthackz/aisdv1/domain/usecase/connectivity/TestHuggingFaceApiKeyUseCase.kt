package com.shifthackz.aisdv1.domain.usecase.connectivity

interface TestHuggingFaceApiKeyUseCase {
    suspend operator fun invoke(): Boolean
}
