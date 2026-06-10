package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

/**
 * Executes the `function` step in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
fun interface FetchHuggingFaceModelsUseCase {

    suspend operator fun invoke(): List<HuggingFaceModel>
}
