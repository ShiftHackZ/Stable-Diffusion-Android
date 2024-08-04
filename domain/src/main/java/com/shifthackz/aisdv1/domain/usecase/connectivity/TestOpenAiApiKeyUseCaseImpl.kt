package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.OpenAiGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TestOpenAiApiKeyUseCaseImpl(
    private val openAiGenerationRepository: OpenAiGenerationRepository,
) : TestOpenAiApiKeyUseCase {

    override fun invoke(): Single<Boolean> = openAiGenerationRepository.validateApiKey()
}
