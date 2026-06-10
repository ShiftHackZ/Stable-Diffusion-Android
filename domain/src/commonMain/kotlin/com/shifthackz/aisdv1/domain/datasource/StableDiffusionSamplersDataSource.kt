package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials

sealed interface StableDiffusionSamplersDataSource {

    interface Remote : StableDiffusionSamplersDataSource {
        suspend fun fetchSamplers(
            baseUrl: String,
            credentials: AuthorizationCredentials,
        ): List<StableDiffusionSampler>
    }

    interface Local : StableDiffusionSamplersDataSource {
        suspend fun getSamplers(): List<StableDiffusionSampler>
        suspend fun insertSamplers(samplers: List<StableDiffusionSampler>)
    }
}
