package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.EmbeddingsDataSource
import com.shifthackz.aisdv1.domain.datasource.SwarmUiSessionDataSource
import com.shifthackz.aisdv1.domain.entity.Embedding
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.EmbeddingsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

internal class EmbeddingsRepositoryImpl(
    private val rdsA1111: EmbeddingsDataSource.Remote.Automatic1111,
    private val rdsSwarm: EmbeddingsDataSource.Remote.SwarmUi,
    private val swarmSession: SwarmUiSessionDataSource,
    private val lds: EmbeddingsDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : EmbeddingsRepository {

    override fun fetchEmbeddings(): Completable = when (preferenceManager.source) {
        ServerSource.AUTOMATIC1111 -> rdsA1111
            .fetchEmbeddings()
            .flatMapCompletable(lds::insertEmbeddings)

        ServerSource.SWARM_UI -> swarmSession
            .getSessionId()
            .flatMap(rdsSwarm::fetchEmbeddings)
            .let(swarmSession::handleSessionError)
            .flatMapCompletable(lds::insertEmbeddings)

        else -> Completable.complete()
    }

    override fun fetchAndGetEmbeddings(): Single<List<Embedding>> = fetchEmbeddings()
        .onErrorComplete()
        .andThen(lds.getEmbeddings())

    override fun getEmbeddings(): Single<List<Embedding>> = lds.getEmbeddings()
}
