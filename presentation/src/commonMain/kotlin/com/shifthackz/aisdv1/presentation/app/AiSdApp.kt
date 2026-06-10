package com.shifthackz.aisdv1.presentation.app

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.extensions.clearFocusOnTap
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.common.schedulers.DispatchersProvider
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.core.validation.common.CommonStringValidator
import com.shifthackz.aisdv1.core.validation.dimension.DimensionValidator
import com.shifthackz.aisdv1.core.validation.path.FilePathValidator
import com.shifthackz.aisdv1.core.validation.url.UrlValidator
import com.shifthackz.aisdv1.domain.feature.work.BackgroundTaskManager
import com.shifthackz.aisdv1.domain.feature.work.BackgroundWorkObserver
import com.shifthackz.aisdv1.domain.usecase.caching.DataPreLoaderUseCase
import com.shifthackz.aisdv1.domain.usecase.caching.SaveLastResultToCacheUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DeleteModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.DownloadModelUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalMediaPipeModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.downloadable.GetLocalOnnxModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.SaveGenerationResultUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ImageToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.GetRandomImageUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.InterruptGenerationUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveHordeProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.ObserveLocalDiffusionProcessStatusUseCase
import com.shifthackz.aisdv1.domain.usecase.generation.TextToImageUseCase
import com.shifthackz.aisdv1.domain.usecase.huggingface.FetchHuggingFaceModelsUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToA1111UseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHordeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToHuggingFaceUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToLocalDiffusionUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToMediaPipeUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToOpenAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToStabilityAiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.ConnectToSwarmUiUseCase
import com.shifthackz.aisdv1.domain.usecase.settings.GetConfigurationUseCase
import com.shifthackz.aisdv1.domain.usecase.sdsampler.GetStableDiffusionSamplersUseCase
import com.shifthackz.aisdv1.domain.usecase.splash.SplashNavigationUseCase
import com.shifthackz.aisdv1.domain.usecase.gallery.GetAllGalleryUseCase
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.core.GenerationFormUpdateEvent
import com.shifthackz.aisdv1.presentation.core.GenerationPlatformServices
import com.shifthackz.aisdv1.presentation.platform.rememberExternalUrlLauncher
import com.shifthackz.aisdv1.presentation.platform.ExternalUrlLauncher
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuScreen
import com.shifthackz.aisdv1.presentation.screen.donate.DonateScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryScreen
import com.shifthackz.aisdv1.presentation.screen.history.HistoryScreenContent
import com.shifthackz.aisdv1.presentation.screen.history.HistoryViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageContent
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageInPaintScreenContent
import com.shifthackz.aisdv1.presentation.screen.img2img.rememberImageToImagePlatformActions
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderScreenContent
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderViewModel
import com.shifthackz.aisdv1.presentation.screen.loader.toContentState
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerScreen
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingContent
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingViewModel
import com.shifthackz.aisdv1.presentation.screen.report.ReportScreen
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreen
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupDownloadGuard
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupEffect
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupContent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashScreenContent
import com.shifthackz.aisdv1.presentation.screen.splash.SplashViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSharer
import com.shifthackz.aisdv1.presentation.screen.txt2img.ImageSaver
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageContent
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageViewModel
import com.shifthackz.aisdv1.presentation.screen.txt2img.decodeBase64ImageBitmap
import com.shifthackz.aisdv1.presentation.screen.web.webui.WebUiScreen
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppTheme
import androidx.compose.material3.MaterialTheme
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

/**
 * Renders the `AiSdApp` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
fun AiSdApp() {
    val koin = remember { initKoin() }
    val preferenceManager = remember(koin) { koin.get<PreferenceManager>() }
    remember(preferenceManager) {
        Localization.setLanguageCode(preferenceManager.languageCode.takeIf(String::isNotBlank))
        Unit
    }
    val router = remember { RootAppRouter() }
    val route by router.route.collectAsState()
    val drawerOpen by router.drawerOpen.collectAsState()
    val canNavigateBack by router.canNavigateBack.collectAsState()
    val languageCode by Localization.languageCodeFlow.collectAsState()
    val urlLauncher = rememberExternalUrlLauncher()
    val imageToImagePlatformActions = rememberImageToImagePlatformActions()
    val textToImageViewModel = remember(koin, router) {
        koin.get<TextToImageViewModel> {
            parametersOf(router)
        }
    }
    val imageToImageViewModel = remember(koin, router, imageToImagePlatformActions) {
        koin.get<ImageToImageViewModel> {
            parametersOf(router, imageToImagePlatformActions)
        }
    }
    val visitedHomeRoutes = remember { mutableStateListOf<AppRoute>() }
    var lastHomeRoute by remember { mutableStateOf<AppRoute?>(null) }
    val currentRoute = route
    val selectedHomeRoute = currentRoute.asHomeTabRoute()

    LaunchedEffect(selectedHomeRoute) {
        selectedHomeRoute?.let { homeRoute ->
            lastHomeRoute = homeRoute
            if (!visitedHomeRoutes.contains(homeRoute)) {
                visitedHomeRoutes.add(homeRoute)
            }
        }
    }

    RootBackHandler(
        enabled = drawerOpen || canNavigateBack,
        onBack = router::navigateBack,
    )

    AiSdAppTheme {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .clearFocusOnTap(),
            color = MaterialTheme.colorScheme.background,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                val shellHomeRoute = selectedHomeRoute ?: lastHomeRoute
                val renderedHomeRoutes = (visitedHomeRoutes + listOfNotNull(shellHomeRoute)).distinct()

                if (shellHomeRoute != null) {
                    HomeRoutesScaffold(
                        languageCode = languageCode,
                        activeRoute = shellHomeRoute,
                        renderedRoutes = renderedHomeRoutes,
                        router = router,
                        buildInfoProvider = koin.get<BuildInfoProvider>(),
                        preferenceManager = preferenceManager,
                        textToImageViewModel = textToImageViewModel,
                        imageToImageViewModel = imageToImageViewModel,
                    )
                }

                if (selectedHomeRoute == null) {
                    OverlayRouteContent(
                        languageCode = languageCode,
                        route = currentRoute,
                        koin = koin,
                        router = router,
                        urlLauncher = urlLauncher,
                        imageToImageViewModel = imageToImageViewModel,
                    )
                }
            }
        }
    }
}

