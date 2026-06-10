package com.shifthackz.aisdv1.domain.interactor.wakelock

import com.shifthackz.aisdv1.domain.usecase.wakelock.AcquireWakelockUseCase
import com.shifthackz.aisdv1.domain.usecase.wakelock.ReleaseWakeLockUseCase

internal data class WakeLockInterActorImpl(
    override val acquireWakelockUseCase: AcquireWakelockUseCase,
    override val releaseWakeLockUseCase: ReleaseWakeLockUseCase
) : WakeLockInterActor
