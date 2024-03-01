package com.shifthackz.aisdv1.presentation.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.core.app.ActivityCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shifthackz.aisdv1.core.common.extensions.copyToClipboard
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.presentation.features.FileSharingFeature
import com.shifthackz.aisdv1.presentation.features.GalleryExportZip
import com.shifthackz.aisdv1.presentation.features.GalleryGridItemClick
import com.shifthackz.aisdv1.presentation.features.GalleryItemImageShare
import com.shifthackz.aisdv1.presentation.features.GalleryItemInfoShare
import com.shifthackz.aisdv1.presentation.features.ImagePickerFeature
import com.shifthackz.aisdv1.presentation.features.ReportProblemEmailComposer
import com.shifthackz.aisdv1.presentation.features.SettingsConfigurationClick
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuScreen
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
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf

class AiStableDiffusionActivity : ComponentActivity(), ImagePickerFeature, FileSharingFeature {

    private val galleryDetailSharing: GalleryDetailSharing by inject()
    private val analytics: Analytics by inject()
    private val debugMenuAccessor: DebugMenuAccessor by inject()

    private val viewModel: AiStableDiffusionViewModel by viewModel()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        debugLog("Notification permission is ${if (granted) "GRANTED" else "DENIED"}.")
    }

    private val storagePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) viewModel.onStoragePermissionsGranted()
        debugLog("Storage permission is ${result}.")
    }

    override val fileProviderDescriptor: FileProviderDescriptor by inject()

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        analytics.initialize()
        requestNotificationPermission()
        requestStoragePermission()
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
                                launchManageStoragePermission = ::setupManageStoragePermission,
                            )
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
                            launchIntent = ::startActivity,
                            launchSetup = {
                                analytics.logEvent(SettingsConfigurationClick)
                                navController.navigate(
                                    "${Constants.ROUTE_SERVER_SETUP}/${ServerSetupLaunchSource.SETTINGS.key}"
                                )
                            },
                            launchUrl = ::openUrl,
                            launchDebugMenu = {
                                if (debugMenuAccessor.invoke()) {
                                    navController.navigate(Constants.ROUTE_DEBUG)
                                }
                            },
                            shareLogFile = {
                                ReportProblemEmailComposer().invoke(this@AiStableDiffusionActivity)
                            },
                            requestStoragePermissions = {
                                val hasPermission = requestStoragePermission()
                                if (hasPermission) viewModel.onStoragePermissionsGranted()
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
                                copyToClipboard = { text -> copyToClipboard(text) }
                            ).Build()
                        }

                        composable(Constants.ROUTE_DEBUG) {
                            DebugMenuScreen(
                                viewModel = koinViewModel(),
                                onNavigateBack = { navController.navigateUp() },
                            ).Build()
                        }
                    }
                }
            }
        }
    }

    private fun setupManageStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val intent = Intent(ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            startActivity(intent)
        } else {
            val hasPermission = requestStoragePermission()
            if (hasPermission) {
                Toast.makeText(this, "Already granted", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun requestStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) return false
        val missingPermissions = buildList {
            if (ActivityCompat.checkSelfPermission(
                    this@AiStableDiffusionActivity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (ActivityCompat.checkSelfPermission(
                    this@AiStableDiffusionActivity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }
        if (missingPermissions.isEmpty()) return true
        storagePermission.launch(missingPermissions.toTypedArray())
        return false
    }
}
