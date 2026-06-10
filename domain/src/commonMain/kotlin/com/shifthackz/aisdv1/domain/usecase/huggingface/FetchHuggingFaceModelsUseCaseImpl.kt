package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import com.shifthackz.aisdv1.domain.repository.HuggingFaceModelsRepository

/**
 * Implements `FetchHuggingFaceModelsUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
class FetchHuggingFaceModelsUseCaseImpl(
    /**
     * Exposes the `repository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val repository: HuggingFaceModelsRepository,
) : FetchHuggingFaceModelsUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    override suspend fun invoke(): List<HuggingFaceModel> =
        repository.fetchAndGetHuggingFaceModels()
}
