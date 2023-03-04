package com.shifthackz.aisdv1.domain.usecase.sdmodel

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import io.reactivex.rxjava3.core.Single

interface GetStableDiffusionModelsUseCase {
    operator fun invoke(): Single<List<Pair<StableDiffusionModelDomain, Boolean>>>
}
