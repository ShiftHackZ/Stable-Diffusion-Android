package com.shifthackz.aisdv1.app.di

import android.content.Intent
import com.shifthackz.aisdv1.app.BuildConfig
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.SchedulersProvider
import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationCredentials
import com.shifthackz.aisdv1.domain.feature.auth.AuthorizationStore
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.diffusion.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.diffusion.environment.DeviceNNAPIFlagProvider
import com.shifthackz.aisdv1.feature.diffusion.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.network.qualifiers.ApiKeyProvider
import com.shifthackz.aisdv1.network.qualifiers.ApiUrlProvider
import com.shifthackz.aisdv1.network.qualifiers.CredentialsProvider
import com.shifthackz.aisdv1.network.qualifiers.NetworkHeaders
import com.shifthackz.aisdv1.network.qualifiers.NetworkPrefixes
import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionActivity
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import java.util.Date
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Needed for retrofit builder, because it will crash at runtime if baseUrl is not set
 */
private const val DEFAULT_SERVER_URL = "http://127.0.0.1"
private const val DEFAULT_HORDE_API_KEY = "0000000000"

val providersModule = module {

    single<ApiUrlProvider> {
        object : ApiUrlProvider {
            override val stableDiffusionAutomaticApiUrl: String = DEFAULT_SERVER_URL
            override val stableDiffusionAppApiUrl: String = BuildConfig.UPDATE_API_URL
            override val hordeApiUrl: String = BuildConfig.HORDE_AI_URL
            override val imageCdnApiUrl: String = BuildConfig.IMAGE_CDN_URL
            override val huggingFaceApiUrl: String = BuildConfig.HUGGING_FACE_URL
            override val huggingFaceInferenceApiUrl = BuildConfig.HUGGING_FACE_INFERENCE_URL
            override val openAiApiUrl: String = BuildConfig.OPEN_AI_URL
            override val stabilityAiApiUrl = BuildConfig.STABILITY_AI_URL
        }
    }

    single {
        ApiKeyProvider {
            val preference = get<PreferenceManager>()
            when (preference.source) {
                ServerSource.HORDE -> {
                    val key =
                        preference.hordeApiKey.takeIf(String::isNotEmpty) ?: DEFAULT_HORDE_API_KEY
                    NetworkHeaders.API_KEY to key
                }

                ServerSource.HUGGING_FACE -> {
                    val key = "${NetworkPrefixes.BEARER} ${preference.huggingFaceApiKey}"
                    NetworkHeaders.AUTHORIZATION to key
                }

                ServerSource.OPEN_AI -> {
                    val key = "${NetworkPrefixes.BEARER} ${preference.openAiApiKey}"
                    NetworkHeaders.AUTHORIZATION to key
                }

                ServerSource.STABILITY_AI -> {
                    val key = "${NetworkPrefixes.BEARER} ${preference.stabilityAiApiKey}"
                    NetworkHeaders.AUTHORIZATION to key
                }

                else -> null
            }
        }
    }

    single<CredentialsProvider> {
        object : CredentialsProvider {
            override fun invoke(): CredentialsProvider.Data {
                val store = get<AuthorizationStore>()
                return when (val credentials = store.getAuthorizationCredentials()) {
                    is AuthorizationCredentials.HttpBasic -> CredentialsProvider.Data.HttpBasic(
                        login = credentials.login,
                        password = credentials.password,
                    )

                    else -> CredentialsProvider.Data.None
                }
            }
        }
    }

    single<LinksProvider> {
        object : LinksProvider {
            override val hordeUrl: String = BuildConfig.HORDE_AI_URL
            override val hordeSignUpUrl: String = BuildConfig.HORDE_AI_SIGN_UP_URL
            override val huggingFaceUrl: String = BuildConfig.HUGGING_FACE_INFO_URL
            override val openAiInfoUrl: String = BuildConfig.OPEN_AI_INFO_URL
            override val stabilityAiInfoUrl: String = BuildConfig.STABILITY_AI_INFO_URL
            override val privacyPolicyUrl: String = BuildConfig.POLICY_URL
            override val donateUrl: String = BuildConfig.DONATE_URL
            override val gitHubSourceUrl: String = BuildConfig.GITHUB_SOURCE_URL
            override val setupInstructionsUrl: String = BuildConfig.SETUP_INSTRUCTIONS_URL
            override val swarmUiInfoUrl: String = BuildConfig.SWARM_UI_INFO_URL
            override val demoModeUrl: String = BuildConfig.DEMO_MODE_API_URL
        }
    }

    single<BuildInfoProvider> {
        object : BuildInfoProvider {
            override val isDebug: Boolean = BuildConfig.DEBUG
            override val buildNumber: Int = BuildConfig.VERSION_CODE
            override val version: BuildVersion = BuildVersion(BuildConfig.VERSION_NAME)
            override val type: BuildType = BuildType.fromBuildConfig(BuildConfig.BUILD_FLAVOR_TYPE)

            override fun toString(): String = buildString {
                append("$version")
                if (BuildConfig.DEBUG) append("-dev")
                append(" ($buildNumber)")
                if (type == BuildType.FOSS) append(" FOSS")
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

    single<TimeProvider> {
        object : TimeProvider {
            override fun nanoTime(): Long = System.nanoTime()
            override fun currentTimeMillis(): Long = System.currentTimeMillis()
            override fun currentDate(): Date = Date()
        }
    }

    single<FileProviderDescriptor> {
        object : FileProviderDescriptor {
            override val providerPath: String = "${androidApplication().packageName}.fileprovider"
            override val imagesCacheDirPath: String = "${androidApplication().cacheDir}/images"
            override val logsCacheDirPath: String = "${androidApplication().cacheDir}/logs"
            override val localModelDirPath: String = "${androidApplication().filesDir.absolutePath}/model"
            override val workCacheDirPath: String = "${androidApplication().cacheDir}/work"
        }
    }

    single {
        DeviceNNAPIFlagProvider {
            get<PreferenceManager>().localUseNNAPI
                .let { nnApi -> if (nnApi) LocalDiffusionFlag.NN_API else LocalDiffusionFlag.CPU }
                .let(LocalDiffusionFlag::value)
        }
    }

    single {
        LocalModelIdProvider { get<PreferenceManager>().localModelId }
    }

    single {
        ActivityIntentProvider {
            Intent(androidContext(), AiStableDiffusionActivity::class.java)
        }
    }
}
