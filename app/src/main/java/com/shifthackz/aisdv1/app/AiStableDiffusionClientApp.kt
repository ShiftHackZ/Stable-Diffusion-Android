package com.shifthackz.aisdv1.app

import android.app.Application
import com.shifthackz.aisdv1.app.di.preferenceModule
import com.shifthackz.aisdv1.app.di.providersModule
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

class AiStableDiffusionClientApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    private fun initializeKoin() = startKoin {
        androidContext(this@AiStableDiffusionClientApp)
        modules(
            demoModule,
            preferenceModule,
            providersModule,
            domainModule,
            *dataModule,
            networkModule,
            databaseModule,
            validatorsModule,
            imageProcessingModule,
            *presentationModule,
        )
    }
}
