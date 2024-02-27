package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.StableDiffusionModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface StableDiffusionModelsDataSource {

    interface Remote : StableDiffusionModelsDataSource {
        fun fetchSdModels(): Single<List<StableDiffusionModel>>
    }

    interface Local : StableDiffusionModelsDataSource {
        fun getModels(): Single<List<StableDiffusionModel>>
        fun insertModels(models: List<StableDiffusionModel>): Completable
    }
}
