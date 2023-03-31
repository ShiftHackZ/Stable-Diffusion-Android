package com.shifthackz.aisdv1.domain.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

sealed interface CoinDataSource {

    interface Local : CoinDataSource {
        fun querySpentCoinsForPeriod(start: Long, end: Long): Single<Int>
        fun onCoinSpent(): Completable
    }

    interface Remote : CoinDataSource {
        fun fetchCoinsConfig(): Single<Int>
    }
}
