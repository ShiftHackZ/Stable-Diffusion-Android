package com.shifthackz.aisdv1.app.di

import android.content.Intent
import com.shifthackz.aisdv1.app.BuildConfig
import com.shifthackz.aisdv1.core.common.appbuild.ActivityIntentProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.appbuild.BuildVersion
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.feature.onnx.entity.LocalDiffusionFlag
import com.shifthackz.aisdv1.feature.onnx.environment.DeviceNNAPIFlagProvider
import com.shifthackz.aisdv1.feature.onnx.environment.LocalModelIdProvider
import com.shifthackz.aisdv1.presentation.activity.AiStableDiffusionActivity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val providersModule = module {

    single<LinksProvider> {
        object : LinksProvider {
            override val hordeUrl: String = BuildConfig.HORDE_AI_URL
            override val hordeSignUpUrl: String = BuildConfig.HORDE_AI_SIGN_UP_URL
            override val huggingFaceUrl: String = BuildConfig.HUGGING_FACE_INFO_URL
            override val openAiInfoUrl: String = BuildConfig.OPEN_AI_INFO_URL
            override val stabilityAiInfoUrl: String = BuildConfig.STABILITY_AI_INFO_URL
            override val falAiInfoUrl: String = BuildConfig.FAL_AI_INFO_URL
            override val privacyPolicyUrl: String = BuildConfig.POLICY_URL
            override val donateUrl: String = BuildConfig.DONATE_URL
            override val projectWebsiteUrl: String = BuildConfig.PROJECT_WEBSITE_URL
            override val developerWebsiteUrl: String = BuildConfig.DEVELOPER_WEBSITE_URL
            override val gitHubSourceUrl: String = BuildConfig.GITHUB_SOURCE_URL
            override val licenseUrl: String = BuildConfig.LICENSE_URL
            override val setupInstructionsUrl: String = BuildConfig.SETUP_INSTRUCTIONS_URL
            override val swarmUiInfoUrl: String = BuildConfig.SWARM_UI_INFO_URL
            override val demoModeUrl: String = BuildConfig.DEMO_MODE_API_URL
            override val telegramCommunityUrl: String = "https://t.me/sdai_app"
            override val discordCommunityUrl: String = "https://discord.gg/jzdR9m8Ves"
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
                when (type) {
                    BuildType.FULL -> append(" FULL")
                    BuildType.FOSS -> append(" FOSS")
                    BuildType.PLAY -> Unit
                }
            }
        }
    }

    single<DispatchersProvider> {
        object : DispatchersProvider {
            override val io: CoroutineDispatcher = Dispatchers.IO
            override val ui: CoroutineDispatcher = Dispatchers.Main
            override val immediate: CoroutineDispatcher = Dispatchers.Main.immediate
        }
    }

    single<TimeProvider> {
        object : TimeProvider {
            override fun nanoTime(): Long = System.nanoTime()
            override fun currentTimeMillis(): Long = System.currentTimeMillis()
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
            get<PreferenceManager>().localOnnxUseNNAPI
                .let { nnApi -> if (nnApi) LocalDiffusionFlag.NN_API else LocalDiffusionFlag.CPU }
                .let(LocalDiffusionFlag::value)
        }
    }

    single {
        LocalModelIdProvider { get<PreferenceManager>().localOnnxModelId }
    }

    single {
        ActivityIntentProvider {
            Intent(androidContext(), AiStableDiffusionActivity::class.java)
        }
    }
}
