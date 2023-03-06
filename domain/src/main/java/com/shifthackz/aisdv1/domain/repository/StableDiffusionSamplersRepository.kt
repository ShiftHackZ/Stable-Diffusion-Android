package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionSampler
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionSamplersRepository {
    fun fetchSamplers(): Completable

    fun getSamplers(): Single<List<StableDiffusionSampler>>
}
