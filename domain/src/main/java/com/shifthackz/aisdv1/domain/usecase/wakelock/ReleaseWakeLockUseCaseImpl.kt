package com.shifthackz.aisdv1.domain.usecase.wakelock

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

internal class ReleaseWakeLockUseCaseImpl(
    private val wakeLockRepository: WakeLockRepository,
) : ReleaseWakeLockUseCase {

    override fun invoke() = runCatching {
        wakeLockRepository.wakeLock.release()
    }.onFailure { t ->
        errorLog(t)
    }
}
