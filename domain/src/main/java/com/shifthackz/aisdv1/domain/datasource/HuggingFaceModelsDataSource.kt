package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface HuggingFaceModelsDataSource {

    interface Remote : HuggingFaceModelsDataSource {
        fun fetchHuggingFaceModels(): Single<List<HuggingFaceModel>>
    }

    interface Local : HuggingFaceModelsDataSource{
        fun getAll(): Single<List<HuggingFaceModel>>
        fun save(models: List<HuggingFaceModel>): Completable
    }
}
