package com.shifthackz.aisdv1.data.local

import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.storage.db.coins.dao.CoinDao
import com.shifthackz.aisdv1.storage.db.coins.entity.CoinEntity
import io.reactivex.rxjava3.core.Completable
import java.util.*

internal class CoinLocalDataSource(
    private val coinDao: CoinDao,
) : CoinDataSource.Local {

    override fun querySpentCoinsForPeriod(start: Long, end: Long) = coinDao
        .queryAvailableCoinsForPeriod(start, end)
        .map { it.size }

    override fun onCoinSpent(): Completable = coinDao.insert(
        CoinEntity(0L, Date())
    )
}
