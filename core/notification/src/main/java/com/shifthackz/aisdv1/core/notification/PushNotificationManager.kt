package com.shifthackz.aisdv1.core.notification

import android.app.Notification
import androidx.core.app.NotificationCompat
import com.shifthackz.aisdv1.core.model.UiText

interface PushNotificationManager {

    fun createAndShowInstant(title: UiText, body: UiText)

    fun createAndShowInstant(title: String, body: String)

    fun show(id: Int, notification: Notification)

    fun createNotification(
        title: UiText,
        body: UiText?,
        block: NotificationCompat.Builder.() -> Unit = {},
    ): Notification

    fun createNotification(
        title: String,
        body: String?,
        block: NotificationCompat.Builder.() -> Unit = {},
    ): Notification

    fun createNotificationChannel()
}
