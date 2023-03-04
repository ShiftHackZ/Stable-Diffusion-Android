package com.shifthackz.aisdv1.domain.repository

import io.reactivex.rxjava3.core.Completable

interface StableDiffusionSamplersRepository {
    fun fetchSamplers(): Completable
}
