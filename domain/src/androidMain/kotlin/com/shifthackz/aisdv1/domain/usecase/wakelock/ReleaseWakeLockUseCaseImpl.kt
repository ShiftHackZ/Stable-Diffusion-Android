package com.shifthackz.aisdv1.domain.usecase.wakelock

import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.domain.repository.WakeLockRepository

/**
 * Implements `ReleaseWakeLockUseCase` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal class ReleaseWakeLockUseCaseImpl(
    /**
     * Exposes the `wakeLockRepository` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    private val wakeLockRepository: WakeLockRepository,
) : ReleaseWakeLockUseCase {

    /**
     * Executes the `invoke` step in the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override fun invoke() = runCatching {
        wakeLockRepository.wakeLock.release()
    }.onFailure { t ->
        errorLog(t)
    }
}
