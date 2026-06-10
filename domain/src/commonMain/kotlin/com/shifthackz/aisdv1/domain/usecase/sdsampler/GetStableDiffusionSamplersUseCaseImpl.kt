package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.repository.StableDiffusionSamplersRepository

class GetStableDiffusionSamplersUseCaseImpl(
    private val repository: StableDiffusionSamplersRepository,
) : GetStableDiffusionSamplersUseCase {

    override suspend operator fun invoke(): List<StableDiffusionSampler> {
        runCatching { repository.fetchSamplers() }
        return repository.getSamplers()
    }
}
