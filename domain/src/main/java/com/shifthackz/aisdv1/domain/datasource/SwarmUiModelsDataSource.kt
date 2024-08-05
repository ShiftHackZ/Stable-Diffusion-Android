package com.shifthackz.aisdv1.domain.datasource

import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface SwarmUiModelsDataSource {

    interface Remote : SwarmUiModelsDataSource {
        fun fetchSwarmModels(sessionId: String): Single<List<SwarmUiModel>>
    }

    interface Local : SwarmUiModelsDataSource {
        fun getModels(): Single<List<SwarmUiModel>>
        fun insertModels(models: List<SwarmUiModel>): Completable
    }
}
