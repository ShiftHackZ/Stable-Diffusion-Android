package com.shifthackz.aisdv1.notification.di

import androidx.core.app.NotificationManagerCompat
import com.shifthackz.aisdv1.notification.PushNotificationManager
import com.shifthackz.aisdv1.notification.PushNotificationManagerImpl
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val notificationModule = module {
    factory { NotificationManagerCompat.from(androidContext()) }
    factory<PushNotificationManager> { PushNotificationManagerImpl(androidContext(), get()) }
}
