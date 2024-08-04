package com.shifthackz.aisdv1.domain.usecase.connectivity

import com.shifthackz.aisdv1.domain.repository.HuggingFaceGenerationRepository
import io.reactivex.rxjava3.core.Single

internal class TestHuggingFaceApiKeyUseCaseImpl(
    private val huggingFaceGenerationRepository: HuggingFaceGenerationRepository,
) : TestHuggingFaceApiKeyUseCase {

    override fun invoke(): Single<Boolean> = huggingFaceGenerationRepository.validateApiKey()
}
