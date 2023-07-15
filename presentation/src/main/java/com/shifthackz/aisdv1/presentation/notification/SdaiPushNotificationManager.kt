package com.shifthackz.aisdv1.presentation.notification

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.extensions.isAppInForeground
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionActivity

class SdaiPushNotificationManager(
    private val context: Context,
    private val manager: NotificationManagerCompat
) {

    @SuppressLint("MissingPermission")
    fun show(title: UiText, body: UiText) {
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
        createNotificationChannel()
        debugLog("Show PN => title: $title, body: $body")
        manager.notify(System.currentTimeMillis().toInt(), notification)

    }

    private fun createNotification(title: UiText, body: UiText) = with(
        NotificationCompat.Builder(context, SDAI_NOTIFICATION_CHANNEL_ID)
    ) {
        setSmallIcon(R.drawable.ic_notification)
        setContentTitle(title.asString(context))
        setContentText(body.asString(context))
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setAutoCancel(true)
        setContentIntent(
            PendingIntent.getActivity(
                context,
                0,
                Intent(context, AiStableDiffusionActivity::class.java).also { intent ->
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                },
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
            ),
        )
        build()
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
