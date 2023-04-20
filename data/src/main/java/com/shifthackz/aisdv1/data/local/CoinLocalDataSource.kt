package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.storage.db.coins.dao.CoinDao
import com.shifthackz.aisdv1.storage.db.coins.dao.EarnedCoinDao
import com.shifthackz.aisdv1.storage.db.coins.entity.CoinEntity
import com.shifthackz.aisdv1.storage.db.coins.entity.EarnedCoinEntity
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import java.util.*

internal class CoinLocalDataSource(
    private val coinDao: CoinDao,
    private val earnedCoinDao: EarnedCoinDao,
) : CoinDataSource.Local {

    override fun querySpentDailyCoinsForPeriod(start: Long, end: Long) = coinDao
        .queryAvailableCoinsForPeriod(start, end)
        .map { it.size }

    override fun queryEarnedCoins(): Single<Int> = earnedCoinDao.queryCount()

    override fun onDailyCoinSpent(): Completable = coinDao.insert(CoinEntity(0L, Date()))

    override fun onEarnedCoinSpent(): Completable = earnedCoinDao.deleteLast()

    override fun onEarnedCoinsRewarded(amount: Int): Completable = (1..amount)
        .map { EarnedCoinEntity(0L, Date()) }
        .let(earnedCoinDao::insert)
}
