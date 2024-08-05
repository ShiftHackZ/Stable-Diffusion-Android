package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.LorasDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.LoRA
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.LorasRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class LorasRepositoryImpl(
    private val rdsA1111: LorasDataSource.Remote.Automatic1111,
    private val rdsSwarm: LorasDataSource.Remote.SwarmUi,
    private val swarmSession: SwarmUiSessionDataSource,
    private val lds: LorasDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : LorasRepository {

    override fun fetchLoras(): Completable = when (preferenceManager.source) {
        ServerSource.AUTOMATIC1111 -> rdsA1111
            .fetchLoras()
            .flatMapCompletable(lds::insertLoras)

        ServerSource.SWARM_UI -> swarmSession
            .getSessionId()
            .flatMap(rdsSwarm::fetchLoras)
            .let(swarmSession::handleSessionError)
            .flatMapCompletable(lds::insertLoras)

        else -> Completable.complete()
    }

    override fun fetchAndGetLoras(): Single<List<LoRA>> = fetchLoras()
        .onErrorComplete()
        .andThen(getLoras())

    override fun getLoras(): Single<List<LoRA>> = lds.getLoras()
}
