package com.shifthackz.aisdv1.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface CoinRepository {
    fun observeAvailableCoinsForToday(): Flowable<Int>
    fun spendCoin(): Completable
}
