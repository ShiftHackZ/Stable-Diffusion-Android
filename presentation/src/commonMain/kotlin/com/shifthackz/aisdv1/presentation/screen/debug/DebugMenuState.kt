package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.mvi.MviState

data class DebugMenuState(
    val screenModal: DebugMenuModal = DebugMenuModal.None,
    val localDiffusionAllowCancel: Boolean = false,
    val localDiffusionSchedulerThread: SchedulersToken = SchedulersToken.COMPUTATION,
    val showWorkManagerSection: Boolean = false,
    val showLocalDiffusionSection: Boolean = false,
    val showQualityAssuranceSection: Boolean = true,
) : MviState

sealed interface DebugMenuModal {
    data object None : DebugMenuModal
    data class LDScheduler(val scheduler: SchedulersToken) : DebugMenuModal
}
