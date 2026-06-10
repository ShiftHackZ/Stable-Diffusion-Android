package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
import com.shifthackz.aisdv1.domain.interactor.wakelock.WakeLockInterActor
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

class AndroidGenerationPlatformServices(
    private val notificationManager: PushNotificationManager,
    private val wakeLockInterActor: WakeLockInterActor,
) : GenerationPlatformServices {

    override val supportsBackgroundGeneration: Boolean = true

    override suspend fun acquireWakeLock() {
        wakeLockInterActor.acquireWakelockUseCase()
    }

    override suspend fun releaseWakeLock() {
        wakeLockInterActor.releaseWakeLockUseCase()
    }

    override fun showGenerationSucceeded() {
        notificationManager.createAndShowInstant(
            LocalizationR.string.notification_finish_title.asUiText(),
            LocalizationR.string.notification_finish_sub_title.asUiText(),
        )
    }

    override fun showGenerationFailed() {
        notificationManager.createAndShowInstant(
            LocalizationR.string.notification_fail_title.asUiText(),
            LocalizationR.string.notification_fail_sub_title.asUiText(),
        )
    }
}
