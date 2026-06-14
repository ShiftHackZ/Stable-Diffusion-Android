package com.shifthackz.aisdv1.data.repository

import com.shifthackz.aisdv1.data.mappers.toDomain
import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import com.shifthackz.aisdv1.domain.repository.NetworkUsageRepository
import com.shifthackz.aisdv1.network.client.NetworkUsageCounter
import com.shifthackz.aisdv1.storage.db.persistent.dao.NetworkUsageDao
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

/**
 * Room-backed implementation of network usage counters used by Settings.
 *
 * Ktor calls publish byte deltas through [NetworkUsageCounter], while model downloaders call
 * [increment] directly. Both paths end up in the same Room table, which keeps the network usage
 * screen observable and durable across process restarts until the user explicitly resets it.
 *
 * @param dao Room DAO that owns atomic bucket increments and live traffic snapshots.
 *
 * @author Dmitriy Moroz
 */
internal class NetworkUsageRepositoryImpl(
    private val dao: NetworkUsageDao,
) : NetworkUsageRepository {

    private val scope = CoroutineScope(
        SupervisorJob() + Dispatchers.Default + CoroutineExceptionHandler { _, _ -> },
    )

    init {
        NetworkUsageCounter.recorder = { category, bytes ->
            enqueueIncrement(category.toDomain(), bytes)
        }
    }

    override fun observe(): Flow<NetworkUsage> = dao
        .observe()
        .map { it.toDomain() }

    override suspend fun get(): NetworkUsage = dao.query().toDomain()

    override suspend fun increment(bucket: NetworkUsageBucket, bytes: Long) {
        val safeBytes = bytes.coerceAtLeast(0L)
        if (safeBytes == 0L) return
        dao.addBytes(bucket.key, safeBytes)
    }

    override fun enqueueIncrement(bucket: NetworkUsageBucket, bytes: Long) {
        val safeBytes = bytes.coerceAtLeast(0L)
        if (safeBytes == 0L) return
        scope.launch {
            dao.addBytes(bucket.key, safeBytes)
        }
    }

    override suspend fun reset() {
        dao.deleteAll()
    }
}
