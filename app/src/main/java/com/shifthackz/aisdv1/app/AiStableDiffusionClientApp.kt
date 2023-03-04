package com.shifthackz.aisdv1.app

import android.app.Application
import com.shifthackz.aisdv1.app.di.providersModule
import com.shifthackz.aisdv1.data.di.dataModule
import com.shifthackz.aisdv1.data.di.localDataSourceModule
import com.shifthackz.aisdv1.data.di.remoteDataSourceModule
import com.shifthackz.aisdv1.data.di.repositoryModule
import com.shifthackz.aisdv1.domain.di.domainModule
import com.shifthackz.aisdv1.network.di.networkModule
import com.shifthackz.aisdv1.presentation.di.viewModelModule
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
            providersModule,
            domainModule,
            *dataModule,
            networkModule,
            databaseModule,
            viewModelModule,
        )
    }
}
