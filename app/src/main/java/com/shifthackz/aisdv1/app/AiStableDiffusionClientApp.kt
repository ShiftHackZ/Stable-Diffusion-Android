package com.shifthackz.aisdv1.app

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.shifthackz.aisdv1.app.di.featureModule
import com.shifthackz.aisdv1.app.di.preferenceModule
import com.shifthackz.aisdv1.app.di.providersModule
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.imageprocessing.di.imageProcessingModule
import com.shifthackz.aisdv1.core.validation.di.validatorsModule
import com.shifthackz.aisdv1.data.di.dataModule
import com.shifthackz.aisdv1.demo.di.demoModule
import com.shifthackz.aisdv1.domain.di.domainModule
import com.shifthackz.aisdv1.network.di.networkModule
import com.shifthackz.aisdv1.presentation.di.presentationModule
import com.shifthackz.aisdv1.storage.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class AiStableDiffusionClientApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StrictMode.setVmPolicy(VmPolicy.Builder().build())
        initializeKoin()
        initializeLogging()
    }

    private fun initializeKoin() = startKoin {
        androidContext(this@AiStableDiffusionClientApp)
        modules(
            demoModule,
            *featureModule,
            preferenceModule,
            providersModule,
            *domainModule,
            *dataModule,
            networkModule,
            databaseModule,
            validatorsModule,
            imageProcessingModule,
            *presentationModule,
        )
    }

    private fun initializeLogging() {
        Timber.plant(FileLoggingTree())
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
