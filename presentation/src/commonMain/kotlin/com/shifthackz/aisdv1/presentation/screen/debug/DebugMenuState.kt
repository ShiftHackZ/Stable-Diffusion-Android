package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.schedulers.SchedulersToken
import com.shifthackz.aisdv1.core.mvi.MviState

/**
 * Carries `DebugMenuState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class DebugMenuState(
    /**
     * Exposes the `screenModal` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val screenModal: DebugMenuModal = DebugMenuModal.None,
    /**
     * Exposes the `localDiffusionAllowCancel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionAllowCancel: Boolean = false,
    /**
     * Exposes the `localDiffusionSchedulerThread` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionSchedulerThread: SchedulersToken = SchedulersToken.COMPUTATION,
    /**
     * Exposes the `showWorkManagerSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showWorkManagerSection: Boolean = false,
    /**
     * Exposes the `showLocalDiffusionSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showLocalDiffusionSection: Boolean = false,
    /**
     * Exposes the `showQualityAssuranceSection` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showQualityAssuranceSection: Boolean = true,
) : MviState

/**
 * Defines the `DebugMenuModal` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
sealed interface DebugMenuModal {
    /**
     * Provides the `None` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    data object None : DebugMenuModal
    /**
     * Carries `LDScheduler` data through the SDAI presentation layer.
     *
     * @param scheduler scheduler value consumed by the API.
     * @author Dmitriy Moroz
     */
    data class LDScheduler(val scheduler: SchedulersToken) : DebugMenuModal
}
