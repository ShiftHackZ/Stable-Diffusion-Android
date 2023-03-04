package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModelDomain
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionModelsDataSource {

    interface Remote : StableDiffusionModelsDataSource {
        fun fetchSdModels(): Single<List<StableDiffusionModelDomain>>
    }

    interface Local : StableDiffusionModelsDataSource {
        fun insertModels(models: List<StableDiffusionModelDomain>): Completable
        fun getModels(): Single<List<StableDiffusionModelDomain>>
    }
}
