package com.shifthackz.aisdv1.domain.usecase.sdmodel

interface SelectStableDiffusionModelUseCase {
    suspend operator fun invoke(modelName: String)
}
