package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.extensions.openMarket
import com.shifthackz.aisdv1.core.extensions.openUrl
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.presentation.features.*
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailSharing
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.home.homeScreenNavGraph
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreen
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashScreen
import com.shifthackz.aisdv1.presentation.theme.AiStableDiffusionAppTheme
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.version.VersionCheckerComposable
import com.shifthackz.aisdv1.presentation.widget.version.VersionCheckerViewModel
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AiStableDiffusionActivity : ComponentActivity(), ImagePickerFeature, FileSharingFeature {

    private val galleryDetailSharing: GalleryDetailSharing by inject()
    private val adFeature: AdFeature by inject()
    private val analytics: Analytics by inject()

    private val versionCheckerViewModel: VersionCheckerViewModel by viewModel()

    override val fileProviderDescriptor: FileProviderDescriptor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adFeature.initialize(this)
        analytics.initialize()
        setContent {
            val navController = rememberNavController()
            AiStableDiffusionAppTheme {
                Box {
                    NavHost(
                        navController = navController,
                        startDestination = Constants.ROUTE_SPLASH,
                    ) {
                        composable(Constants.ROUTE_SPLASH) {
                            SplashScreen(
                                viewModel = koinViewModel(),
                                navigateOnBoarding = {},
                                navigateServerSetup = {
                                    navController.navigate(
                                        "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SPLASH.key}"
                                    ) {
                                        popUpTo(Constants.ROUTE_SPLASH) {
                                            inclusive = true
                                        }
                                    }
                                },
                                navigateHome = {
                                    navController.navigate(Constants.ROUTE_CONFIG_LOADER) {
                                        popUpTo(Constants.ROUTE_SPLASH) {
                                            inclusive = true
                                        }
                                    }
                                },
                            ).Build()
                        }

                        composable(
                            route = Constants.ROUTE_SERVER_SETUP_FULL,
                            arguments = listOf(
                                navArgument(Constants.PARAM_SOURCE) { type = NavType.IntType }
                            )
                        ) { entry ->
                            val sourceKey = entry.arguments
                                ?.getInt(Constants.PARAM_SOURCE)
                                ?: ServerSetupLaunchSource.SPLASH.key
                            val viewModel = getViewModel<ServerSetupViewModel>(
                                parameters = { parametersOf(sourceKey) }
                            )
                            ServerSetupScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() },
                                onServerSetupComplete = {
                                    navController.navigate(Constants.ROUTE_HOME) {
                                        popUpTo(Constants.ROUTE_SERVER_SETUP) {
                                            inclusive = true
                                        }
                                    }
                                },
                                launchUrl = ::openUrl,
                            ).Build()
                        }

                        composable(Constants.ROUTE_CONFIG_LOADER) {
                            ConfigurationLoaderScreen(
                                viewModel = koinViewModel(),
                                onNavigateNextScreen = {
                                    navController.navigate(Constants.ROUTE_HOME) {
                                        popUpTo(Constants.ROUTE_CONFIG_LOADER) {
                                            inclusive = true
                                        }
                                    }
                                },
                            ).Build()
                        }

                        homeScreenNavGraph(
                            route = Constants.ROUTE_HOME,
                            pickImage = { clb -> pickPhoto(this@AiStableDiffusionActivity, clb) },
                            takePhoto = { clb -> takePhoto(this@AiStableDiffusionActivity, clb) },
                            shareGalleryFile = { zipFile ->
                                analytics.logEvent(GalleryExportZip)
                                shareFile(
                                    context = this@AiStableDiffusionActivity,
                                    file = zipFile,
                                    mimeType = Constants.MIME_TYPE_ZIP,
                                )
                            },
                            openGalleryItemDetails = { galleryItemId ->
                                analytics.logEvent(GalleryGridItemClick)
                                navController
                                    .navigate("${Constants.ROUTE_GALLERY_DETAIL}/$galleryItemId")
                            },
                            launchSetup = {
                                analytics.logEvent(SettingsConfigurationClick)
                                navController.navigate(
                                    "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SETTINGS.key}"
                                )
                            },
                            launchUpdateCheck = {
                                analytics.logEvent(SettingsCheckUpdate)
                                versionCheckerViewModel.checkForUpdate(true)
                            },
                            launchInAppReview = {
                                analytics.logEvent(SettingsOpenMarket)
                                openMarket()
                            },
                            launchUrl = ::openUrl,
                            shareLogFile = {
                                ReportProblemEmailComposer().invoke(this@AiStableDiffusionActivity)
                            },
                        )

                        composable(
                            route = Constants.ROUTE_GALLERY_DETAIL_FULL,
                            arguments = listOf(
                                navArgument(Constants.PARAM_ITEM_ID) { type = NavType.LongType },
                            ),
                        ) { entry ->
                            val itemId = entry.arguments?.getLong(Constants.PARAM_ITEM_ID) ?: -1L
                            val viewModel = getViewModel<GalleryDetailViewModel>(
                                parameters = { parametersOf(itemId) }
                            )
                            GalleryDetailScreen(
                                viewModel = viewModel,
                                onNavigateBack = { navController.navigateUp() },
                                shareGalleryFile = { jpgFile ->
                                    analytics.logEvent(GalleryItemImageShare)
                                    shareFile(
                                        context = this@AiStableDiffusionActivity,
                                        file = jpgFile,
                                        mimeType = Constants.MIME_TYPE_JPG,
                                    )
                                },
                                shareGenerationParams = { uiState ->
                                    analytics.logEvent(GalleryItemInfoShare)
                                    galleryDetailSharing(
                                        context = this@AiStableDiffusionActivity,
                                        state = uiState,
                                    )
                                },
                            ).Build()
                        }
                    }
                    VersionCheckerComposable(
                        viewModel = versionCheckerViewModel,
                        launchMarket = { openMarket() },
                    ).Build()
                }
            }
        }
    }
}
