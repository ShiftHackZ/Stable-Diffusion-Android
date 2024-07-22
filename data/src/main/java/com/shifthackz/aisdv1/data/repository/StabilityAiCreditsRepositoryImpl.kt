package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.StabilityAiCreditsDataSource
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.StabilityAiCreditsRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single

internal class StabilityAiCreditsRepositoryImpl(
    private val remoteDataSource: StabilityAiCreditsDataSource.Remote,
    private val localDataSource: StabilityAiCreditsDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : StabilityAiCreditsRepository {

    override fun fetch() = checkServerSource(
        onValid = remoteDataSource
            .fetch()
            .flatMapCompletable(localDataSource::save),
        onNotValid = Completable.error(IllegalStateException("Wrong server source selected.")),
    )

    override fun fetchAndGet() = checkServerSource(
        onValid = fetch().onErrorComplete().andThen(get()),
        onNotValid = Single.error(IllegalStateException("Wrong server source selected.")),
    )

    override fun fetchAndObserve() = checkServerSource(
        onValid = fetch().onErrorComplete().andThen(observe()),
        onNotValid = Flowable.error(IllegalStateException("Wrong server source selected.")),
    )

    override fun get() = checkServerSource(
        onValid = localDataSource.get(),
        onNotValid = Single.error(IllegalStateException("Wrong server source selected.")),
    )

    override fun observe() = checkServerSource(
        onValid = localDataSource.observe(),
        onNotValid = Flowable.error(IllegalStateException("Wrong server source selected.")),
    )

    private fun <T : Any> checkServerSource(
        onValid: T,
        onNotValid: T,
    ): T = when (preferenceManager.source) {
        ServerSource.STABILITY_AI -> onValid
        else -> onNotValid
    }
}
