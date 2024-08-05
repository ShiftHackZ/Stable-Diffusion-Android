package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.SwarmUiModelsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.SwarmUiModel
import com.shifthackz.aisdv1.domain.repository.SwarmUiModelsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class SwarmUiModelsRepositoryImpl(
    private val session: SwarmUiSessionDataSource,
    private val rds: SwarmUiModelsDataSource.Remote,
    private val lds: SwarmUiModelsDataSource.Local,
) : SwarmUiModelsRepository {

    override fun fetchModels(): Completable = session
        .getSessionId()
        .flatMap(rds::fetchSwarmModels)
        .let(session::handleSessionError)
        .flatMapCompletable(lds::insertModels)

    override fun fetchAndGetModels(): Single<List<SwarmUiModel>> = fetchModels()
        .onErrorComplete()
        .andThen(getModels())

    override fun getModels(): Single<List<SwarmUiModel>> = lds.getModels()
}
