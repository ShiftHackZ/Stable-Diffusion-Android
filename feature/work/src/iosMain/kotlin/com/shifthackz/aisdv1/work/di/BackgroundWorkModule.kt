package com.shifthackz.aisdv1.work.di

import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.work.BackgroundTaskManagerImpl
import com.shifthackz.aisdv1.work.BackgroundWorkObserverImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Exposes the `backgroundWorkModule` value used by the SDAI background work feature layer.
 *
 * @author Dmitriy Moroz
 */
actual val backgroundWorkModule = module {
    singleOf(::BackgroundWorkObserverImpl) bind BackgroundWorkObserver::class
    singleOf(::BackgroundTaskManagerImpl) bind BackgroundTaskManager::class
}
