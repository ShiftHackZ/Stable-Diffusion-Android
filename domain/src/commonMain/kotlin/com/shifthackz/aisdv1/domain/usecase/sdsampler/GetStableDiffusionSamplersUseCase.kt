package com.shifthackz.aisdv1.domain.usecase.sdsampler

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler

interface GetStableDiffusionSamplersUseCase {
    suspend operator fun invoke(): List<StableDiffusionSampler>
}
