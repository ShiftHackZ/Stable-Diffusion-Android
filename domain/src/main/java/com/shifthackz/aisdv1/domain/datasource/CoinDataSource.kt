package com.shifthackz.aisdv1.domain.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface CoinDataSource {

    interface Local : CoinDataSource {
        fun querySpentDailyCoinsForPeriod(start: Long, end: Long): Single<Int>
        fun queryEarnedCoins(): Single<Int>
        fun onDailyCoinSpent(): Completable
        fun onEarnedCoinSpent(): Completable
        fun onEarnedCoinsRewarded(amount: Int): Completable
    }

    interface Remote : CoinDataSource {
        fun fetchCoinsConfig(): Single<Int>
    }
}
