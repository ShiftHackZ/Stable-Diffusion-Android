package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

class FetchHuggingFaceModelsUseCaseImpl(
    private val repository: HuggingFaceModelsRepository,
) : FetchHuggingFaceModelsUseCase {

    override suspend fun invoke(): List<HuggingFaceModel> =
        repository.fetchAndGetHuggingFaceModels()
}
