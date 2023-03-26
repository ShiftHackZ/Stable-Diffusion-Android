package com.shifthackz.aisdv1.domain.datasource

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

sealed interface CoinDataSource {

    interface Local : CoinDataSource {
        fun observeQuerySpentCoinsForPeriod(start: Long, end: Long): Flowable<Int>
        fun onCoinSpent(): Completable
    }
}
