package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSamplerDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionSamplersDataSource {

    interface Remote : StableDiffusionSamplersDataSource {
        fun fetchSamplers(): Single<List<StableDiffusionSamplerDomain>>
    }

    interface Local : StableDiffusionSamplersDataSource {
        fun insertSamplers(samplers: List<StableDiffusionSamplerDomain>): Completable
    }
}
