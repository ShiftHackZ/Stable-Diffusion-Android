package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import io.reactivex.rxjava3.core.Single

interface GetStableDiffusionSamplersUseCase {
    operator fun invoke(): Single<List<StableDiffusionSampler>>
}
