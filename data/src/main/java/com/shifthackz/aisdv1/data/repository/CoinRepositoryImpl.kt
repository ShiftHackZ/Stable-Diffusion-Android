package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.domain.datasource.CoinDataSource
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.domain.repository.CoinRepository
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

internal class CoinRepositoryImpl(
    private val coinLocalDataSource: CoinDataSource.Local,
    private val preferenceManager: PreferenceManager,
) : CoinRepository {

    private val availableCoinsPerDay = 10

    override fun observeAvailableCoinsForToday(): Flowable<Int> {
        val now = LocalDateTime.now()
        val start = now.with(LocalTime.MIN).toInstant(ZoneOffset.MIN).toEpochMilli()
        val end = now.with(LocalTime.MAX).toInstant(ZoneOffset.MIN).toEpochMilli()
        return coinLocalDataSource
            .observeQuerySpentCoinsForPeriod(start, end)
            .map { availableCoinsPerDay - it }
    }

    override fun spendCoin(): Completable {
        return if (preferenceManager.useSdAiCloud) coinLocalDataSource.onCoinSpent()
        else Completable.complete()
    }
}
