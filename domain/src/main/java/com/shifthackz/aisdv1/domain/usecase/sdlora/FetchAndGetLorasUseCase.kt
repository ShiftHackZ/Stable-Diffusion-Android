package com.shifthackz.aisdv1.domain.usecase.sdlora

import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import io.reactivex.rxjava3.core.Single

interface FetchAndGetLorasUseCase {
    operator fun invoke(): Single<List<StableDiffusionLora>>
}
