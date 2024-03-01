package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository

internal class TestOpenAiApiKeyUseCaseImpl(
    private val openAiGenerationRepository: OpenAiGenerationRepository,
) : TestOpenAiApiKeyUseCase {

    override fun invoke() = openAiGenerationRepository.validateApiKey()
}
