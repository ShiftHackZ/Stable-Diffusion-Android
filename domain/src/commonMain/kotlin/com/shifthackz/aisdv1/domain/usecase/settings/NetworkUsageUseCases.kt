package com.shifthackz.aisdv1.domain.usecase.settings

import com.shifthackz.aisdv1.domain.entity.NetworkUsage
import com.shifthackz.aisdv1.domain.repository.NetworkUsageRepository
import kotlinx.coroutines.flow.Flow

/**
 * Observes durable network usage statistics for the standalone network usage screen and Settings.
 *
 * @author Dmitriy Moroz
 */
fun interface ObserveNetworkUsageUseCase {
    /**
     * Emits the current traffic counters and every later Room update.
     *
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Flow<NetworkUsage>
}

/**
 * Default implementation that keeps presentation unaware of the persistence mechanism.
 *
 * @param repository Durable network usage repository observed by the use case.
 *
 * @author Dmitriy Moroz
 */
class ObserveNetworkUsageUseCaseImpl(
    private val repository: NetworkUsageRepository,
) : ObserveNetworkUsageUseCase {
    override fun invoke(): Flow<NetworkUsage> = repository.observe()
}

/**
 * Clears persisted network usage statistics after explicit user confirmation.
 *
 * @author Dmitriy Moroz
 */
fun interface ResetNetworkUsageUseCase {
    /**
     * Resets all traffic buckets to zero.
     *
     * @author Dmitriy Moroz
     */
    suspend operator fun invoke()
}

/**
 * Default reset implementation backed by [NetworkUsageRepository].
 *
 * @param repository Durable network usage repository cleared by the use case.
 *
 * @author Dmitriy Moroz
 */
class ResetNetworkUsageUseCaseImpl(
    private val repository: NetworkUsageRepository,
) : ResetNetworkUsageUseCase {
    override suspend fun invoke() = repository.reset()
}
