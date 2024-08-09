package com.shifthackz.aisdv1.app

import android.annotation.SuppressLint
import android.app.Application
import android.database.CursorWindow
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.work.Configuration
import androidx.work.WorkManager
import com.shifthackz.aisdv1.app.di.featureModule
import com.shifthackz.aisdv1.app.di.preferenceModule
import com.shifthackz.aisdv1.app.di.providersModule
import com.shifthackz.aisdv1.core.common.log.FileLoggingTree
import com.shifthackz.aisdv1.core.common.log.errorLog
import com.shifthackz.aisdv1.core.imageprocessing.di.imageProcessingModule
import com.shifthackz.aisdv1.core.notification.di.notificationModule
import com.shifthackz.aisdv1.core.validation.di.validatorsModule
import com.shifthackz.aisdv1.data.di.dataModule
import com.shifthackz.aisdv1.demo.di.demoModule
import com.shifthackz.aisdv1.domain.di.domainModule
import com.shifthackz.aisdv1.network.di.networkModule
import com.shifthackz.aisdv1.presentation.di.presentationModule
import com.shifthackz.aisdv1.storage.di.databaseModule
import com.shifthackz.aisdv1.work.di.SdaiWorkerFactory
import com.shifthackz.aisdv1.work.di.backgroundWorkModule
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class AiStableDiffusionClientApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StrictMode.setVmPolicy(VmPolicy.Builder().build())
        Thread.currentThread().setUncaughtExceptionHandler { _, t -> errorLog(t) }
        initializeKoin()
        initializeLogging()
        initializeCursorSize()
        initializeWorkManager()
    }

    /**
     * Overrides the cursor size to prevent Room DB fail with big base64.
     *
     * Reference: https://stackoverflow.com/questions/51959944/sqliteblobtoobigexception-row-too-big-to-fit-into-cursorwindow-requiredpos-0-t
     */
    @SuppressLint("DiscouragedPrivateApi")
    private fun initializeCursorSize() {
        try {
            val field = CursorWindow::class.java.getDeclaredField("sCursorWindowSize")
            field.isAccessible = true
            field.set(null, 100 * 1024 * 1024) // 100 Mb
        } catch (e: Exception) {
            errorLog(e)
        }
    }

    private fun initializeKoin() = startKoin {
        androidContext(this@AiStableDiffusionClientApp)
        modules(
            notificationModule,
            demoModule,
            *featureModule,
            preferenceModule,
            providersModule,
            *domainModule,
            *dataModule,
            backgroundWorkModule,
            networkModule,
            databaseModule,
            validatorsModule,
            imageProcessingModule,
            *presentationModule,
        )
    }

    private fun initializeLogging() {
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        Timber.plant(FileLoggingTree())
    }

    private fun initializeWorkManager() {
        try {
            val workerFactory: SdaiWorkerFactory by inject()
            val configuration = Configuration.Builder()
                .setWorkerFactory(workerFactory)
                .build()

            WorkManager.initialize(this, configuration)
        } catch (e: Exception) {
            errorLog(e)
        }
    }
}
