package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import kotlinx.coroutines.flow.Flow

/**
 * Persists and observes app network usage counters.
 *
 * @author Dmitriy Moroz
 */
interface NetworkUsageRepository {
    /**
     * Observes the current traffic counters and every subsequent Room update.
     *
     * @author Dmitriy Moroz
     */
    fun observe(): Flow<NetworkUsage>

    /**
     * Returns a one-shot snapshot of the persisted traffic counters.
     *
     * @author Dmitriy Moroz
     */
    suspend fun get(): NetworkUsage

    /**
     * Adds a positive byte delta to the requested [bucket] and suspends until it is persisted.
     *
     * @param bucket Traffic bucket that should receive the byte delta.
     * @param bytes Number of bytes to add; non-positive values are ignored by implementations.
     *
     * @author Dmitriy Moroz
     */
    suspend fun increment(bucket: NetworkUsageBucket, bytes: Long)

    /**
     * Adds a positive byte delta in repository-owned background scope.
     *
     * This is used by low-level Ktor helpers where making every response parser suspend on Room
     * bookkeeping would make network code harder to compose.
     *
     * @param bucket Traffic bucket that should receive the byte delta.
     * @param bytes Number of bytes to enqueue; non-positive values are ignored by implementations.
     *
     * @author Dmitriy Moroz
     */
    fun enqueueIncrement(bucket: NetworkUsageBucket, bytes: Long)

    /**
     * Clears all persisted counters after the user confirms reset statistics.
     *
     * @author Dmitriy Moroz
     */
    suspend fun reset()
}
