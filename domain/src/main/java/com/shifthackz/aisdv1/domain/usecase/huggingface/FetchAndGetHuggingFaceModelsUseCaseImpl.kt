package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

internal class FetchAndGetHuggingFaceModelsUseCaseImpl(
    private val huggingFaceModelsRepository: HuggingFaceModelsRepository,
) : FetchAndGetHuggingFaceModelsUseCase {

    override fun invoke() = huggingFaceModelsRepository.fetchAndGetHuggingFaceModels()
}
