package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository
import io.reactivex.rxjava3.core.Single

internal class GetStableDiffusionSamplersUseCaseImpl(
    private val repository: StableDiffusionSamplersRepository,
) : GetStableDiffusionSamplersUseCase {

    override operator fun invoke(): Single<List<StableDiffusionSampler>> = repository
        .getSamplers()
}
