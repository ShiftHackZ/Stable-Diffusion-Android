package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionLora
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionLorasRepository {
    fun fetchLoras(): Completable
    fun fetchAndGetLoras(): Single<List<StableDiffusionLora>>
    fun getLoras(): Single<List<StableDiffusionLora>>
}
