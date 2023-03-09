package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GallerySharing
import com.shifthackz.aisdv1.presentation.screen.home.homeScreenNavGraph
import com.shifthackz.aisdv1.presentation.screen.splash.SplashLoaderScreen
import com.shifthackz.aisdv1.presentation.theme.AiStableDiffusionAppTheme
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.utils.ImagePickerCapability
import org.koin.android.ext.android.inject
import org.koin.androidx.compose.getViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class AiStableDiffusionActivity : ComponentActivity(), ImagePickerCapability {

    private val gallerySharing: GallerySharing by inject()

    override val fileProviderDescriptor: FileProviderDescriptor by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            AiStableDiffusionAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = Constants.ROUTE_SPLASH,
                ) {
                    composable(Constants.ROUTE_SPLASH) {
                        SplashLoaderScreen(
                            viewModel = koinViewModel(),
                            onNavigateNextScreen = {
                                navController.navigate(Constants.ROUTE_HOME) {
                                    popUpTo(Constants.ROUTE_SPLASH) {
                                        inclusive = true
                                    }
                                }
                            }
                        ).Build()
                    }

                    homeScreenNavGraph(
                        route = Constants.ROUTE_HOME,
                        pickImage = { clb -> pickPhoto(this@AiStableDiffusionActivity, clb) },
                        takePhoto = { clb -> takePhoto(this@AiStableDiffusionActivity, clb) },
                        shareGalleryFile = { zipFile ->
                            gallerySharing(
                                context = this@AiStableDiffusionActivity,
                                file = zipFile,
                                mimeType = Constants.MIME_TYPE_ZIP,
                            )
                        },
                        openGalleryItemDetails = { galleryItemId ->
                            navController
                                .navigate("${Constants.ROUTE_GALLERY_DETAIL}/$galleryItemId")
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
                                gallerySharing(
                                    context = this@AiStableDiffusionActivity,
                                    file = jpgFile,
                                    mimeType = Constants.MIME_TYPE_JPG,
                                )
                            },
                        ).Build()
                    }
                }
            }
        }
    }
}
