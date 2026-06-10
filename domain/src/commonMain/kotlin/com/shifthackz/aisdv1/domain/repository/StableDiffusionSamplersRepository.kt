package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler

interface StableDiffusionSamplersRepository {
    suspend fun fetchSamplers()

    suspend fun getSamplers(): List<StableDiffusionSampler>
}
