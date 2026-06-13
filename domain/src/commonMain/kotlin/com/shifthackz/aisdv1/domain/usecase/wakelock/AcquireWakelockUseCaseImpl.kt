package com.shifthackz.aisdv1.domain.usecase.wakelock

import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

/**
 * Implements `AcquireWakelockUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class AcquireWakelockUseCaseImpl(
    /**
     * Exposes the `wakeLockRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val wakeLockRepository: WakeLockRepository,
) : AcquireWakelockUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @param timeout timeout value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun invoke(timeout: Long) = runCatching {
        wakeLockRepository.wakeLock.acquire(timeout)
    }
}
