package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

/**
 * Coordinates `AndroidGenerationPlatformServices` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class AndroidGenerationPlatformServices(
    /**
     * Exposes the `notificationManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val notificationManager: PushNotificationManager,
    /**
     * Exposes the `wakeLockInterActor` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val wakeLockInterActor: WakeLockInterActor,
) : GenerationPlatformServices {

    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = true

    /**
     * Performs the SDAI side effect handled by `acquireWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun acquireWakeLock() {
        wakeLockInterActor.acquireWakelockUseCase()
    }

    /**
     * Performs the SDAI side effect handled by `releaseWakeLock`.
     *
     * @author Dmitriy Moroz
     */
    override suspend fun releaseWakeLock() {
        wakeLockInterActor.releaseWakeLockUseCase()
    }

    /**
     * Executes the `showGenerationSucceeded` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showGenerationSucceeded() {
        notificationManager.createAndShowInstant(
            LocalizationR.string.notification_finish_title.asUiText(),
            LocalizationR.string.notification_finish_sub_title.asUiText(),
        )
    }

    /**
     * Executes the `showGenerationFailed` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showGenerationFailed() {
        notificationManager.createAndShowInstant(
            LocalizationR.string.notification_fail_title.asUiText(),
            LocalizationR.string.notification_fail_sub_title.asUiText(),
        )
    }
}
