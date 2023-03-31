package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.core.common.extensions.getDayRange
import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.preference.SessionPreference
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.PublishSubject
import java.util.*
import java.util.concurrent.TimeUnit

internal class CoinRepositoryImpl(
    private val coinLocalDataSource: CoinDataSource.Local,
    private val coinRemoteDateSource: CoinDataSource.Remote,
    private val preferenceManager: PreferenceManager,
    private val sessionPreference: SessionPreference,
) : CoinRepository {

    private val eventCoinSpent: PublishSubject<Unit> = PublishSubject.create()

    override fun observeAvailableCoinsForToday(): Flowable<Int> {
        val coinsPerDayProducer: () -> Single<Int> = {
            val availableCoinsPerDay = sessionPreference.coinsPerDay
            if (availableCoinsPerDay != -1) Single.just(availableCoinsPerDay)
            else coinRemoteDateSource
                .fetchCoinsConfig()
                .doOnSuccess { sessionPreference.coinsPerDay = it }
        }

        val coinsCalculationProducer: () -> Flowable<Int> = {
            val (start, end) = Date().getDayRange()
            coinsPerDayProducer()
                .zipWith(
                    coinLocalDataSource.querySpentCoinsForPeriod(start.time, end.time),
                    ::Pair,
                )
                .map { (availableToday, spentToday) -> availableToday - spentToday }
                .map { coins -> if (coins < 0) 0 else coins }
                .toFlowable()
        }

        val coinSpentEventProducer: () -> Flowable<*> = {
            eventCoinSpent.toFlowable(BackpressureStrategy.LATEST)
        }

        val coreTickProducer: () -> Flowable<*> = {
            Flowable.interval(10L, TimeUnit.SECONDS)
        }

        return Flowable
            .merge(coinsCalculationProducer(), coreTickProducer(), coinSpentEventProducer())
            .flatMap { coinsCalculationProducer() }
            .replay(1)
            .refCount(1, TimeUnit.SECONDS)
    }

    override fun spendCoin(): Completable {
        eventCoinSpent.onNext(Unit)
        return if (preferenceManager.useSdAiCloud) coinLocalDataSource.onCoinSpent()
        else Completable.complete()
    }
}
