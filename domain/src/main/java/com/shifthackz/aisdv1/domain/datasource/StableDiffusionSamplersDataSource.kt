package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionSamplersDataSource {

    interface Remote : StableDiffusionSamplersDataSource {
        fun fetchSamplers(): Single<List<StableDiffusionSampler>>
    }

    interface Local : StableDiffusionSamplersDataSource {
        fun getSamplers(): Single<List<StableDiffusionSampler>>
        fun insertSamplers(samplers: List<StableDiffusionSampler>): Completable
    }
}
