package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.shifthackz.aisdv1.core.common.file.FileProviderDescriptor
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailViewModel
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GallerySharing
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationItem
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageScreen
import com.shifthackz.aisdv1.presentation.screen.splash.SplashLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
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

                composable(Constants.ROUTE_HOME) {
                    HomeNavigationScreen(
                        navItems = listOf(
                            HomeNavigationItem(
                                name = stringResource(R.string.home_tab_txt_to_img),
                                route = Constants.ROUTE_TXT_TO_IMG,
                                icon = HomeNavigationItem.Icon.Resource(
                                    resId = R.drawable.ic_text,
                                    modifier = Modifier.size(24.dp),
                                ),
                                content = {
                                    TextToImageScreen(viewModel = koinViewModel()).Build()
                                },
                            ),
                            HomeNavigationItem(
                                name = stringResource(R.string.home_tab_img_to_img),
                                route = Constants.ROUTE_IMG_TO_IMG,
                                icon = HomeNavigationItem.Icon.Resource(
                                    resId = R.drawable.ic_image,
                                    modifier = Modifier.size(24.dp),
                                ),
                                content = {
                                    ImageToImageScreen(
                                        viewModel = koinViewModel(),
                                        pickImage = {
                                            pickPhoto(this@AiStableDiffusionActivity, it)
                                        },
                                        takePhoto = {
                                            takePhoto(this@AiStableDiffusionActivity, it)
                                        },
                                    ).Build()
                                },
                            ),
                            HomeNavigationItem(
                                name = stringResource(R.string.home_tab_gallery),
                                route = Constants.ROUTE_GALLERY,
                                icon = HomeNavigationItem.Icon.Resource(
                                    resId = R.drawable.ic_gallery,
                                    modifier = Modifier.size(24.dp),
                                ),
                                content = {
                                    GalleryScreen(
                                        viewModel = koinViewModel(),
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
                                        }
                                    ).Build()
                                },
                            )
                        ),
                    ).Build()
                }

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
