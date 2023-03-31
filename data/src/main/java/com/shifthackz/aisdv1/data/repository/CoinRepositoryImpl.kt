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

    private val eventCoinsUpdated: PublishSubject<Unit> = PublishSubject.create()

    override fun observeAvailableCoins(): Flowable<Int> {
        val coinsCalculationProducer: () -> Flowable<Int> = {
            Single.zip(
                calculateAvailableDailyCoins(),
                calculateAvailableEarnedCoins(),
                ::Pair,
            )
                .map { (daily, earned) -> daily + earned }
                .toFlowable()
        }

        val coinsUpdatedEventProducer: () -> Flowable<*> = {
            eventCoinsUpdated.toFlowable(BackpressureStrategy.LATEST)
        }

        val coreTickProducer: () -> Flowable<*> = {
            Flowable.interval(5L, TimeUnit.SECONDS)
        }

        return Flowable
            .merge(coinsCalculationProducer(), coreTickProducer(), coinsUpdatedEventProducer())
            .flatMap { coinsCalculationProducer() }
            .replay(1)
            .refCount(1, TimeUnit.SECONDS)
    }

    override fun spendCoin(): Completable {
        eventCoinsUpdated.onNext(Unit)
        return if (preferenceManager.useSdAiCloud) {
            calculateAvailableDailyCoins()
                .flatMapCompletable {
                    if (it > 0) coinLocalDataSource.onDailyCoinSpent()
                    else coinLocalDataSource.onEarnedCoinSpent()
                }
                .doOnComplete { eventCoinsUpdated.onNext(Unit) }
        }
        else Completable.complete()
    }

    override fun earnCoins(amount: Int) = coinLocalDataSource.onEarnedCoinsRewarded(amount)

    private fun getAvailableCoinsPerDay(): Single<Int> {
        val availableCoinsPerDay = sessionPreference.coinsPerDay
        return if (availableCoinsPerDay != -1) Single.just(availableCoinsPerDay)
        else coinRemoteDateSource
            .fetchCoinsConfig()
            .doOnSuccess { sessionPreference.coinsPerDay = it }
    }

    private fun calculateAvailableDailyCoins(): Single<Int> {
        val (start, end) = Date().getDayRange()
        return getAvailableCoinsPerDay()
            .zipWith(
                coinLocalDataSource.querySpentDailyCoinsForPeriod(start.time, end.time),
                ::Pair,
            )
            .map { (availableToday, spentToday) -> availableToday - spentToday }
            .map { coins -> if (coins < 0) 0 else coins }
    }

    private fun calculateAvailableEarnedCoins(): Single<Int> =
        coinLocalDataSource.queryEarnedCoins()
}
