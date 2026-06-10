package com.shifthackz.aisdv1.domain.usecase.huggingface

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel

fun interface FetchHuggingFaceModelsUseCase {

    suspend operator fun invoke(): List<HuggingFaceModel>
}
