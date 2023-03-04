package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface StableDiffusionModelsRepository {
    fun fetchModels(): Completable
    fun fetchAndGetModels(): Single<List<StableDiffusionModelDomain>>
    fun getModels(): Single<List<StableDiffusionModelDomain>>
}
