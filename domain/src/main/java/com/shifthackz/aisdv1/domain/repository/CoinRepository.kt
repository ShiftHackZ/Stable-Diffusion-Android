package com.shifthackz.aisdv1.domain.repository

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

interface CoinRepository {
    fun observeAvailableCoins(): Flowable<Int>
    fun spendCoin(): Completable
    fun earnCoins(amount: Int): Completable
}
