package com.shifthackz.aisdv1.work.core

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.extensions.isAppInForeground
import com.shifthackz.aisdv1.core.notification.PushNotificationManager

/**
 * Coordinates `NotificationWorker` behavior in the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
internal abstract class NotificationWorker(
    context: Context,
    workerParameters: WorkerParameters,
    /**
     * Exposes the `pushNotificationManager` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val pushNotificationManager: PushNotificationManager,
    /**
     * Exposes the `activityIntentProvider` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    private val activityIntentProvider: ActivityIntentProvider,
) : CoroutineWorker(context, workerParameters) {

    /**
     * Exposes the `notificationId` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    abstract val notificationId: Int

    /**
     * Exposes the `genericNotificationId` value used by the SDAI background work feature layer.
     *
     * @author Dmitriy Moroz
     */
    abstract val genericNotificationId: Int

    /**
     * Executes the `showGenericNotification` step in the SDAI background work feature layer.
     *
     * @param text text value consumed by the API.
     * @param body body value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun showGenericNotification(text: String, body: String?) {
        pushNotificationManager.createNotificationChannel()
        val notification = pushNotificationManager.createNotification(text, body) {
            setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    activityIntentProvider().also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
            setVibrate(longArrayOf(10000L, 2000L, 1000L))
            setAutoCancel(true)
            setTicker(text)
            setSilent(applicationContext.isAppInForeground())
        }
        pushNotificationManager.show(genericNotificationId, notification)
    }

    /**
     * Executes the `setForegroundNotification` step in the SDAI background work feature layer.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param subText sub text value consumed by the API.
     * @param progress progress value consumed by the API.
     * @param silent silent value consumed by the API.
     * @param canCancel can cancel value consumed by the API.
     * @author Dmitriy Moroz
     */
    fun setForegroundNotification(
        title: String,
        body: String,
        subText: String? = null,
        progress: Pair<Int, Int>? = null,
        silent: Boolean = false,
        canCancel: Boolean = false,
    ) {
        pushNotificationManager.createNotificationChannel()
        val notification = pushNotificationManager.createProgressNotification(title, body) {
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

            setContentIntent(
                PendingIntent.getActivity(
                    applicationContext,
                    0,
                    activityIntentProvider().also { intent ->
                        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    },
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
                ),
            )
        }

        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ForegroundInfo(notificationId, notification, FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            ForegroundInfo(notificationId, notification)
        }
        setForegroundAsync(info)
    }
}
