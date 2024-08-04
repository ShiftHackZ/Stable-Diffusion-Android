package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository
import io.reactivex.rxjava3.core.Single

internal class FetchAndGetHuggingFaceModelsUseCaseImpl(
    private val huggingFaceModelsRepository: HuggingFaceModelsRepository,
) : FetchAndGetHuggingFaceModelsUseCase {

    override fun invoke(): Single<List<HuggingFaceModel>> = huggingFaceModelsRepository.fetchAndGetHuggingFaceModels()
}
