package com.shifthackz.aisdv1.core.notification

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
import com.shifthackz.aisdv1.core.common.extensions.isAppInForeground
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText

/**
 * Implements `PushNotificationManager` behavior in the SDAI notification layer.
 *
 * @author Dmitriy Moroz
 */
internal class PushNotificationManagerImpl(
    /**
     * Exposes the `context` value used by the SDAI notification layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
    /**
     * Exposes the `manager` value used by the SDAI notification layer.
     *
     * @author Dmitriy Moroz
     */
    private val manager: NotificationManagerCompat,
) : PushNotificationManager {

    /**
     * Creates the SDAI value produced by `createAndShowInstant`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @author Dmitriy Moroz
     */
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

    /**
     * Creates the SDAI value produced by `createAndShowInstant`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun createAndShowInstant(title: String, body: String) {
        createAndShowInstant(title.asUiText(), body.asUiText())
    }

    /**
     * Executes the `show` step in the SDAI notification layer.
     *
     * @param id identifier of the target entity.
     * @param notification notification value consumed by the API.
     * @author Dmitriy Moroz
     */
    @SuppressLint("MissingPermission")
    override fun show(id: Int, notification: Notification) {
        createNotificationChannel()
        manager.notify(id, notification)
    }

    /**
     * Creates the SDAI value produced by `createNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @author Dmitriy Moroz
     */
    override fun createNotification(
        title: UiText,
        body: UiText?,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification = with(
        NotificationCompat.Builder(context, SDAI_NOTIFICATION_CHANNEL_ID)
    ) {
        setSmallIcon(R.drawable.ic_notification)
        setContentTitle(title.asString(context))
        body?.asString(context)?.let {
            setContentText(it)
        }
        apply(block)
        build()
    }

    /**
     * Creates the SDAI value produced by `createNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `createNotification`.
     * @author Dmitriy Moroz
     */
    override fun createNotification(
        title: String,
        body: String?,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification {
        return createNotification(title.asUiText(), body?.asUiText(), block)
    }

    /**
     * Creates the SDAI value produced by `createProgressNotification`.
     *
     * @param title title value consumed by the API.
     * @param body body value consumed by the API.
     * @param block block value consumed by the API.
     * @return Result produced by `createProgressNotification`.
     * @author Dmitriy Moroz
     */
    override fun createProgressNotification(
        title: String,
        body: String?,
        block: NotificationCompat.Builder.() -> Unit
    ): Notification = with(
        NotificationCompat.Builder(context, SDAI_PROGRESS_CHANNEL_ID)
    ) {
        setSmallIcon(R.drawable.ic_notification)
        setContentTitle(title)
        body?.let(::setContentText)
        priority = NotificationCompat.PRIORITY_LOW
        apply(block)
        build()
    }

    /**
     * Creates the SDAI value produced by `createNotificationChannel`.
     *
     * @author Dmitriy Moroz
     */
    override fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
            if (manager.getNotificationChannel(SDAI_PROGRESS_CHANNEL_ID) == null) {
                debugLog("Creating progress notification channel")

                manager.createNotificationChannel(
                    NotificationChannel(
                        SDAI_PROGRESS_CHANNEL_ID,
                        "SDAI Progress",
                        NotificationManager.IMPORTANCE_LOW,
                    ).also { channel ->
                        channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                        channel.setSound(null, null)
                        channel.enableVibration(false)
                    }
                )
            }
        }
    }

    /**
     * Executes the `hasNotificationPermission` step in the SDAI notification layer.
     *
     * @return Result produced by `hasNotificationPermission`.
     * @author Dmitriy Moroz
     */
    private fun hasNotificationPermission(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS)
        } else {
            PackageManager.PERMISSION_GRANTED
        }
    }

    /**
     * Provides the `companion object` singleton used by the SDAI notification layer.
     *
     * @author Dmitriy Moroz
     */
    companion object {
        /**
         * Exposes the `SDAI_NOTIFICATION_CHANNEL_ID` value used by the SDAI notification layer.
         *
         * @author Dmitriy Moroz
         */
        private const val SDAI_NOTIFICATION_CHANNEL_ID = "SDAI_NOTIFICATION_CHANNEL"
        /**
         * Exposes the `SDAI_PROGRESS_CHANNEL_ID` value used by the SDAI notification layer.
         *
         * @author Dmitriy Moroz
         */
        private const val SDAI_PROGRESS_CHANNEL_ID = "SDAI_PROGRESS_CHANNEL"
    }
}
