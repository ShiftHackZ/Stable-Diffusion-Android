package com.shifthackz.aisdv1.domain.interactor.wakelock

import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase

/**
 * Implements `WakeLockInterActor` behavior in the SDAI domain layer.
 *
 * @author Dmitriy Moroz
 */
internal data class WakeLockInterActorImpl(
    /**
     * Exposes the `acquireWakelockUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val acquireWakelockUseCase: AcquireWakelockUseCase,
    /**
     * Exposes the `releaseWakeLockUseCase` value used by the SDAI domain layer.
     *
     * @author Dmitriy Moroz
     */
    override val releaseWakeLockUseCase: ReleaseWakeLockUseCase
) : WakeLockInterActor
