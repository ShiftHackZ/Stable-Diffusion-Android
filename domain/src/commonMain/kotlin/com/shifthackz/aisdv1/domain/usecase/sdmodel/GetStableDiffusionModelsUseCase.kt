package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel

interface GetStableDiffusionModelsUseCase {
    suspend operator fun invoke(): List<Pair<StableDiffusionModel, Boolean>>
}
