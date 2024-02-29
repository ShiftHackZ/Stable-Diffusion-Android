package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository

internal class TestHuggingFaceApiKeyUseCaseImpl(
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
) : TestHuggingFaceApiKeyUseCase {

    override fun invoke() = huggingFaceGenerationRepository.validateApiKey()
}
