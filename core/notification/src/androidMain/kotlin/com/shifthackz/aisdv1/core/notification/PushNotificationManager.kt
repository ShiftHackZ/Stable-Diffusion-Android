package com.shifthackz.aisdv1.core.notification

import android.app.Notification
import androidx.core.app.NotificationCompat
import com.shifthackz.aisdv1.core.model.UiText

/**
 * Defines the `PushNotificationManager` contract for the SDAI notification layer.
 *
 * @author Dmitriy Moroz
 */
interface PushNotificationManager {

    /**
     * Creates the SDAI value produced by `createAndShowInstant`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun createAndShowInstant(title: UiText, body: UiText)

    /**
     * Creates the SDAI value produced by `createAndShowInstant`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun createAndShowInstant(title: String, body: String)

    /**
     * Executes the `show` step in the SDAI notification layer.
     *
     * @param id identifier of the target entity.
     * @param notification notification value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun show(id: Int, notification: Notification)

    /**
     * Creates the SDAI value produced by `createNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `createNotification`.
     * @author Dmitriy Moroz
     */
    fun createNotification(
        title: UiText,
        body: UiText?,
        block: NotificationCompat.Builder.() -> Unit = {},
    ): Notification

    /**
     * Creates the SDAI value produced by `createNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `createNotification`.
     * @author Dmitriy Moroz
     */
    fun createNotification(
        title: String,
        body: String?,
        block: NotificationCompat.Builder.() -> Unit = {},
    ): Notification

    /**
     * Creates the SDAI value produced by `createProgressNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `createProgressNotification`.
     * @author Dmitriy Moroz
     */
    fun createProgressNotification(
        title: String,
        body: String?,
        block: NotificationCompat.Builder.() -> Unit = {},
    ): Notification

    /**
     * Creates the SDAI value produced by `createNotificationChannel`.
     *
     * @author Dmitriy Moroz
     */
    fun createNotificationChannel()
}
