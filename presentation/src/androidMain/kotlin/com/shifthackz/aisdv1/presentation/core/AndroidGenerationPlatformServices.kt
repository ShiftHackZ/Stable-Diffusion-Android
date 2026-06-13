package com.shifthackz.aisdv1.presentation.core

import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.notification.PushNotificationManager
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
) : GenerationPlatformServices {

    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = true

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
