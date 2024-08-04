package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface SwarmUiModelsRepository {
    fun fetchModels(): Completable
    fun fetchAndGetModels(): Single<List<SwarmUiModel>>
    fun getModels(): Single<List<SwarmUiModel>>
}
