package com.shifthackz.aisdv1.work.di

import androidx.work.WorkManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.work.BackgroundTaskManagerImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.bind
import org.koin.dsl.module

val backgroundWorkModule = module {
    factory {
        WorkManagerProvider { WorkManager.getInstance(androidApplication()) }
    }

    factoryOf(::SdaiWorkerFactory)
    factoryOf(::BackgroundTaskManagerImpl) bind BackgroundTaskManager::class
}
