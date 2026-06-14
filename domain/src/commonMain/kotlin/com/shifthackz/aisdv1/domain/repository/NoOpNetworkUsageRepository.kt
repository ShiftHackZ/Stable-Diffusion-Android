package com.shifthackz.aisdv1.domain.repository

import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.entity.NetworkUsageBucket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * Default no-op network usage repository used before data bindings override it.
 *
 * @author Dmitriy Moroz
 */
object NoOpNetworkUsageRepository : NetworkUsageRepository {
    override fun observe(): Flow<NetworkUsage> = flowOf(NetworkUsage())
    override suspend fun get(): NetworkUsage = NetworkUsage()
    override suspend fun increment(bucket: NetworkUsageBucket, bytes: Long) = Unit
    override fun enqueueIncrement(bucket: NetworkUsageBucket, bytes: Long) = Unit
    override suspend fun reset() = Unit
}
