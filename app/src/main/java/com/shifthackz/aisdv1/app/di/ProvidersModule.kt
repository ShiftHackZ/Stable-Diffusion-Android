package com.shifthackz.aisdv1.app.di

import com.shifthackz.aisdv1.app.BuildConfig
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.network.qualifiers.ApiUrlProvider
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Needed for retrofit builder, because it will crash at runtime if baseUrl is not set
 */
private const val DEFAULT_SERVER_URL = "http://127.0.0.1"

val providersModule = module {

    single<ApiUrlProvider> {
        object : ApiUrlProvider {
            override val stableDiffusionAutomaticApiUrl: String = DEFAULT_SERVER_URL
            override val stableDiffusionAppUpdateApiUrl: String = BuildConfig.UPDATE_API_URL
        }
    }

    single<LinksProvider> {
        object : LinksProvider {
            override val privacyPolicyUrl: String = BuildConfig.POLICY_URL
            override val gitHubSourceUrl: String = BuildConfig.GITHUB_SOURCE_URL
            override val setupInstructionsUrl: String = BuildConfig.SETUP_INSTRUCTIONS_URL
            override val demoModeUrl: String = BuildConfig.DEMO_MODE_API_URL
        }
    }

    single<BuildInfoProvider> {
        object : BuildInfoProvider {
            override val isDebug: Boolean = BuildConfig.DEBUG
            override val buildNumber: Int = BuildConfig.VERSION_CODE
            override val version: BuildVersion = BuildVersion(BuildConfig.VERSION_NAME)
            override val buildType: BuildType = BuildType.parse(BuildConfig.BUILD_FLAVOR_TYPE)

            override fun toString(): String = buildString {
                append("$version")
                if (BuildConfig.DEBUG) append("-dev")
                append(" ($buildNumber)")
                buildType.takeIf { it == BuildType.FOSS }?.let { append(" $it") }
            }
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
            override val logsCacheDirPath: String = "${androidApplication().cacheDir}/logs"
        }
    }
}
