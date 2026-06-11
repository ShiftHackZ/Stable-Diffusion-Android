package com.shifthackz.aisdv1.domain.interactor.wakelock

import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase

/**
 * Defines the `WakeLockInterActor` contract for the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
interface WakeLockInterActor {
    /**
     * Exposes the `acquireWakelockUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val acquireWakelockUseCase: AcquireWakelockUseCase
    /**
     * Exposes the `releaseWakeLockUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    val releaseWakeLockUseCase: ReleaseWakeLockUseCase
}
