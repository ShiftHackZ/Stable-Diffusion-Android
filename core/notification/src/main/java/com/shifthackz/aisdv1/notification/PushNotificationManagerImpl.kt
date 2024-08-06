package com.shifthackz.aisdv1.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.extensions.isAppInForeground
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.feature.notification.R

internal class PushNotificationManagerImpl(
    private val context: Context,
    private val manager: NotificationManagerCompat,
) : PushNotificationManager {

    @SuppressLint("MissingPermission")
    override fun createAndShowInstant(title: UiText, body: UiText) {
        val inForeground = context.isAppInForeground()
        if (inForeground) {
            debugLog("App is in foreground, skipping...")
            return
        }

        val permission = hasNotificationPermission()
        if (permission != PackageManager.PERMISSION_GRANTED) {
            debugLog("Missing permissions for POST_NOTIFICATIONS, skipping...")
            return
        }

        val notification = createNotification(title, body)
        debugLog("Show PN => title: $title, body: $body")
        show(System.currentTimeMillis().toInt(), notification)
    }

    override fun createAndShowInstant(title: String, body: String) {
        createAndShowInstant(title.asUiText(), body.asUiText())
    }

    override fun show(id: Int, notification: Notification) {
        createNotificationChannel()
        manager.notify(id, notification)
    }

    override fun createNotification(
        title: UiText,
        body: UiText,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification = with(
        NotificationCompat.Builder(context, SDAI_NOTIFICATION_CHANNEL_ID)
    ) {

        setSmallIcon(R.drawable.ic_notification)
        setContentTitle(title.asString(context))
        setContentText(body.asString(context))
//        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
//        setAutoCancel(true)
        apply(block)
//        setContentIntent(
//            PendingIntent.getActivity(
//                context,
//                0,
//                Intent(context, AiStableDiffusionActivity::class.java).also { intent ->
//                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
//                },
//                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
//            ),
//        )
        build()
    }

    override fun createNotification(
        title: String,
        body: String,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification {
        return createNotification(title.asUiText(), body.asUiText(), block)
    }

    private fun createNotificationChannel() {
        if (manager.getNotificationChannel(SDAI_NOTIFICATION_CHANNEL_ID) == null) {
            debugLog("Creating notification channel")
            manager.createNotificationChannel(
                NotificationChannel(
                    SDAI_NOTIFICATION_CHANNEL_ID,
                    "SDAI Notifications",
                    NotificationManager.IMPORTANCE_HIGH,
                ).also { channel ->
                    channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                }
            )
        }
    }

    private fun hasNotificationPermission(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
    }

    companion object {
        private const val SDAI_NOTIFICATION_CHANNEL_ID = "SDAI_NOTIFICATION_CHANNEL"
    }
}
