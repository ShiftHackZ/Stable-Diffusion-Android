package com.shifthackz.aisdv1.work.core

import android.content.Context
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.notification.PushNotificationManager

internal abstract class NotificationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    private val pushNotificationManager: PushNotificationManager,
) : Rx3Worker(context, workerParameters) {

    abstract val notificationId: Int

    abstract val genericNotificationId: Int

    fun gen(text: String) {
        val notification = pushNotificationManager.createNotification(text, text) {
            setVibrate(longArrayOf(1000L))
            setAutoCancel(true)
            setTicker(text)
        }

        pushNotificationManager.show(genericNotificationId, notification)
    }

    fun setForegroundNotification(
        title: String,
        body: String,
        subText: String? = null,
        progress: Pair<Int, Int>? = null,
        silent: Boolean = false,
        canCancel: Boolean = false,
    ) {
        val notification = pushNotificationManager.createNotification(title, body) {
            setSilent(silent)
            setTicker(title)
            setOngoing(true)

            progress?.let { (step, max) ->
                setProgress(max, step, false)
            } ?: run {
                setProgress(0, 0, true)
            }
            subText?.let { setSubText(it) }

            if (canCancel) {
                val intent = WorkManager
                    .getInstance(applicationContext)
                    .createCancelPendingIntent(id)
                addAction(android.R.drawable.ic_delete, "Cancel", intent)
            }

            setAutoCancel(false)
            setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
        setForegroundAsync(info)
    }
}
