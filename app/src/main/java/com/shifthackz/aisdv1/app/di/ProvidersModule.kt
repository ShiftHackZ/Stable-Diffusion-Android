package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.app.BuildConfig
import com.shifthackz.aisdv1.core.common.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.network.qualifiers.ApiUrlProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

val providersModule = module {

    single<ApiUrlProvider> {
        object : ApiUrlProvider {
            override val stableDiffusionAutomaticApiUrl: String = BuildConfig.SERVER_URL
        }
    }

    single<BuildInfoProvider> {
        object : BuildInfoProvider {
            override val buildNumber: Int = BuildConfig.VERSION_CODE
            override val version: String = BuildConfig.VERSION_NAME

            override fun toString(): String = "$version ($buildNumber)"
        }
    }

    single<SchedulersProvider> {
        object : SchedulersProvider {
            override val io: Scheduler = Schedulers.io()
            override val ui: Scheduler = AndroidSchedulers.mainThread()
            override val computation: Scheduler = Schedulers.computation()
            override val singleThread: Executor = Executors.newSingleThreadExecutor()
        }
    }

    single<FileProviderDescriptor> {
        object : FileProviderDescriptor {
            override val providerPath: String = "${androidApplication().packageName}.fileprovider"
            override val imagesCacheDirPath: String = "${androidApplication().cacheDir}/images"
        }
    }
}
