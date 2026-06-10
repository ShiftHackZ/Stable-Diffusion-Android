package com.shifthackz.aisdv1.presentation.di

import com.shifthackz.aisdv1.core.common.links.DefaultLinksProvider
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.createPlatformBuildInfoProvider
import com.shifthackz.aisdv1.core.common.schedulers.DefaultDispatchersProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.common.time.DefaultTimeProvider
import com.shifthackz.aisdv1.core.common.time.TimeProvider
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.NoOpGenerationPlatformServices
import com.shifthackz.aisdv1.presentation.modal.download.DownloadDialogViewModel
import com.shifthackz.aisdv1.presentation.modal.embedding.EmbeddingViewModel
import com.shifthackz.aisdv1.presentation.modal.extras.ExtrasViewModel
import com.shifthackz.aisdv1.presentation.modal.history.InputHistoryViewModel
import com.shifthackz.aisdv1.presentation.modal.tag.EditTagViewModel
import com.shifthackz.aisdv1.presentation.navigation.router.DonateRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryDetailRouter
import com.shifthackz.aisdv1.presentation.navigation.router.GalleryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HistoryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.HomeRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ImageToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ConfigurationLoaderRouter
import com.shifthackz.aisdv1.presentation.navigation.router.DebugMenuRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpConfigurationLoaderRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpDebugMenuRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpDonateRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpGalleryDetailRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpGalleryRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpImageToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpLoggerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpOnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpReportRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpServerSetupRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpSettingsRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpSplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpTextToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.NoOpWebUiRouter
import com.shifthackz.aisdv1.presentation.navigation.router.LoggerRouter
import com.shifthackz.aisdv1.presentation.navigation.router.OnBoardingRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ReportRouter
import com.shifthackz.aisdv1.presentation.navigation.router.ServerSetupRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.navigation.router.SplashRouter
import com.shifthackz.aisdv1.presentation.navigation.router.TextToImageRouter
import com.shifthackz.aisdv1.presentation.navigation.router.WebUiRouter
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuViewModel
import com.shifthackz.aisdv1.presentation.screen.debug.NoOpDebugMenuPlatformActions
import com.shifthackz.aisdv1.presentation.screen.donate.DonateViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.createDefaultGalleryDetailPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryExportService
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.NoOpGalleryExportService
import com.shifthackz.aisdv1.presentation.screen.gallery.list.NoOpGalleryPlatformActions
import com.shifthackz.aisdv1.presentation.screen.history.HistoryViewModel
import com.shifthackz.aisdv1.presentation.screen.home.HomeViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImagePlatformActions
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.NoOpImageToImagePlatformActions
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.logger.LogReader
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerViewModel
import com.shifthackz.aisdv1.presentation.screen.logger.NoOpLogReader
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingViewModel
import com.shifthackz.aisdv1.presentation.screen.report.ReportViewModel
import com.shifthackz.aisdv1.presentation.screen.settings.NoOpSettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.NoOpServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.createPlatformImageSaver
import com.shifthackz.aisdv1.presentation.screen.web.webui.WebUiViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashViewModel
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppThemeViewModel
import com.shifthackz.aisdv1.presentation.model.ExtraType
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityViewModel
import com.shifthackz.aisdv1.presentation.widget.engine.EngineSelectionViewModel
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkViewModel
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkImageLoader
import com.shifthackz.aisdv1.presentation.widget.work.NoOpBackgroundWorkImageLoader
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import org.koin.dsl.module

import org.koin.core.module.Module

internal fun Module.registerPresentationViewModelBindings() {
    factory {
        AiSdAppThemeViewModel(
            dispatchersProvider = get(),
            preferenceManager = get(),
        )
    }
    factory { (splashRouter: SplashRouter) ->
        SplashViewModel(
            dispatchersProvider = get(),
            splashNavigationUseCase = get(),
            splashRouter = splashRouter,
        )
    }
    factory { (launchSource: LaunchSource, router: OnBoardingRouter) ->
        OnBoardingViewModel(
            launchSource = launchSource,
            dispatchersProvider = get(),
            router = router,
            splashNavigationUseCase = get(),
            preferenceManager = get(),
            buildInfoProvider = get(),
        )
    }
    factory { (router: ConfigurationLoaderRouter) ->
        ConfigurationLoaderViewModel(
            dispatchersProvider = get(),
            dataPreLoaderUseCase = get(),
            getConfigurationUseCase = get(),
            router = router,
        )
    }
    factory { (router: DonateRouter) ->
        DonateViewModel(
            dispatchersProvider = get(),
            fetchSupportersUseCase = get(),
            linksProvider = get(),
            router = router,
        )
    }
    factory { (router: DebugMenuRouter, platformActions: DebugMenuPlatformActions) ->
        DebugMenuViewModel(
            dispatchersProvider = get(),
            preferenceManager = get(),
            debugInsertBadBase64UseCase = get(),
            router = router,
            platformActions = platformActions,
        )
    }
    factory { (router: LoggerRouter) ->
        LoggerViewModel(
            dispatchersProvider = get(),
            logReader = get(),
            router = router,
        )
    }
    factory { (router: SettingsRouter, platformActions: SettingsPlatformActions) ->
        SettingsViewModel(
            dispatchersProvider = get(),
            getStableDiffusionModelsUseCase = get(),
            observeStabilityAiCreditsUseCase = get(),
            selectStableDiffusionModelUseCase = get(),
            clearAppCacheUseCase = get(),
            preferenceManager = get(),
            debugMenuAccessor = get(),
            buildInfoProvider = get(),
            linksProvider = get(),
            router = router,
            platformActions = platformActions,
        )
    }
    factory { (itemId: Long, router: ReportRouter) ->
        ReportViewModel(
            itemId = itemId,
            dispatchersProvider = get(),
            sendReportUseCase = get(),
            getGenerationResultUseCase = get(),
            getLastResultFromCacheUseCase = get(),
            router = router,
            buildInfoProvider = get(),
        )
    }
    factory { (prompt: String, negativePrompt: String, type: ExtraType) ->
        ExtrasViewModel(
            dispatchersProvider = get(),
            fetchAndGetLorasUseCase = get(),
            fetchAndGetHyperNetworksUseCase = get(),
            preferenceManager = get(),
            prompt = prompt,
            negativePrompt = negativePrompt,
            type = type,
        )
    }
    factory { (prompt: String, negativePrompt: String) ->
        EmbeddingViewModel(
            dispatchersProvider = get(),
            fetchAndGetEmbeddingsUseCase = get(),
            preferenceManager = get(),
            prompt = prompt,
            negativePrompt = negativePrompt,
        )
    }
    factory { (prompt: String, negativePrompt: String, tag: String, isNegative: Boolean) ->
        EditTagViewModel(
            dispatchersProvider = get(),
            prompt = prompt,
            negativePrompt = negativePrompt,
            tag = tag,
            isNegative = isNegative,
        )
    }
    factory {
        InputHistoryViewModel(
            dispatchersProvider = get(),
            getGenerationResultPagedUseCase = get(),
        )
    }
    factory { (modelId: String) ->
        DownloadDialogViewModel(
            modelId = modelId,
            getLocalModelUseCase = get(),
            dispatchersProvider = get(),
        )
    }
    factory { (router: TextToImageRouter) ->
        TextToImageViewModel(
            dispatchersProvider = get(),
            getConfigurationUseCase = get(),
            getStableDiffusionSamplersUseCase = get(),
            textToImageUseCase = get(),
            saveGenerationResultUseCase = get(),
            saveLastResultToCacheUseCase = get(),
            interruptGenerationUseCase = get(),
            observeHordeProcessStatusUseCase = get(),
            observeLocalDiffusionProcessStatusUseCase = get(),
            preferenceManager = get(),
            backgroundTaskManager = get(),
            backgroundWorkObserver = get(),
            platformServices = get(),
            buildInfoProvider = get(),
            generationFormUpdateEvent = get(),
            dimensionValidator = get(),
            imageSaver = get(),
            imageSharer = get(),
            router = router,
        )
    }
    factory { (router: ImageToImageRouter, platformActions: ImageToImagePlatformActions) ->
        ImageToImageViewModel(
            dispatchersProvider = get(),
            getConfigurationUseCase = get(),
            getStableDiffusionSamplersUseCase = get(),
            getRandomImageUseCase = get(),
            imageToImageUseCase = get(),
            saveGenerationResultUseCase = get(),
            saveLastResultToCacheUseCase = get(),
            interruptGenerationUseCase = get(),
            observeHordeProcessStatusUseCase = get(),
            observeLocalDiffusionProcessStatusUseCase = get(),
            preferenceManager = get(),
            backgroundTaskManager = get(),
            backgroundWorkObserver = get(),
            platformServices = get(),
            buildInfoProvider = get(),
            generationFormUpdateEvent = get(),
            dimensionValidator = get(),
            imageSaver = get(),
            imageSharer = get(),
            router = router,
            platformActions = platformActions,
        )
    }
    factory { (router: HomeRouter) ->
        HomeViewModel(
            dispatchersProvider = get(),
            getConfigurationUseCase = get(),
            router = router,
        )
    }
    factory { (router: HistoryRouter) ->
        HistoryViewModel(
            dispatchersProvider = get(),
            getAllGalleryUseCase = get(),
            router = router,
        )
    }
    factory { (router: GalleryRouter) ->
        GalleryViewModel(
            dispatchersProvider = get(),
            getMediaStoreInfoUseCase = get(),
            backgroundWorkObserver = get(),
            preferenceManager = get(),
            deleteAllGalleryUseCase = get(),
            deleteGalleryItemsUseCase = get(),
            getGenerationResultPagedUseCase = get(),
            galleryExportService = get(),
            galleryRouter = router,
        )
    }
    factory { (itemId: Long, router: GalleryDetailRouter, platformActions: GalleryDetailPlatformActions) ->
        GalleryDetailViewModel(
            itemId = itemId,
            dispatchersProvider = get(),
            buildInfoProvider = get(),
            getGenerationResultUseCase = get(),
            getLastResultFromCacheUseCase = get(),
            deleteGalleryItemUseCase = get(),
            toggleImageVisibilityUseCase = get(),
            generationFormUpdateEvent = get(),
            router = router,
            platformActions = platformActions,
        )
    }
    factory { (launchSource: LaunchSource, router: ServerSetupRouter) ->
        ServerSetupViewModel(
            launchSource = launchSource,
            dispatchersProvider = get(),
            buildInfoProvider = get(),
            getConfigurationUseCase = get(),
            getLocalOnnxModelsUseCase = get(),
            getLocalMediaPipeModelsUseCase = get(),
            fetchHuggingFaceModelsUseCase = get(),
            urlValidator = get(),
            stringValidator = get(),
            filePathValidator = get(),
            connectToA1111UseCase = get(),
            connectToSwarmUiUseCase = get(),
            connectToHordeUseCase = get(),
            connectToHuggingFaceUseCase = get(),
            connectToLocalDiffusionUseCase = get(),
            connectToMediaPipeUseCase = get(),
            connectToOpenAiUseCase = get(),
            connectToStabilityAiUseCase = get(),
            downloadModelUseCase = get(),
            deleteModelUseCase = get(),
            downloadGuard = get(),
            linksProvider = get(),
            preferenceManager = get(),
            router = router,
        )
    }
    factory {
        EngineSelectionViewModel(
            dispatchersProvider = get(),
            fetchAndGetSwarmUiModelsUseCase = get(),
            observeLocalOnnxModelsUseCase = get(),
            fetchAndGetStabilityAiEnginesUseCase = get(),
            getHuggingFaceModelsUseCase = get(),
            preferenceManager = get(),
            getConfigurationUseCase = get(),
            selectStableDiffusionModelUseCase = get(),
            getStableDiffusionModelsUseCase = get(),
        )
    }
    factory { (router: WebUiRouter) ->
        WebUiViewModel(
            dispatchersProvider = get(),
            preferenceManager = get(),
            router = router,
        )
    }
    factory {
        ConnectivityViewModel(
            dispatchersProvider = get(),
            observeServerConnectivityUseCase = get(),
            getMonitorConnectivityUseCase = get(),
            observeMonitorConnectivityUseCase = get(),
        )
    }
    factory {
        BackgroundWorkViewModel(
            dispatchersProvider = get(),
            backgroundWorkObserver = get(),
            imageLoader = get(),
        )
    }
}
