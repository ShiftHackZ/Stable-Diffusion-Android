package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.HuggingFaceModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface HuggingFaceModelsRepository {
    fun fetchHuggingFaceModels(): Completable
    fun fetchAndGetHuggingFaceModels(): Single<List<HuggingFaceModel>>
    fun getHuggingFaceModels(): Single<List<HuggingFaceModel>>
}
