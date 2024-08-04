package com.shifthackz.aisdv1.domain.usecase.wakelock

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

internal class AcquireWakelockUseCaseImpl(
    private val wakeLockRepository: WakeLockRepository,
) : AcquireWakelockUseCase {

    override fun invoke(timeout: Long): Result<Unit> = runCatching {
        wakeLockRepository.wakeLock.acquire(timeout)
    }.onFailure { t ->
        errorLog(t)
    }
}
