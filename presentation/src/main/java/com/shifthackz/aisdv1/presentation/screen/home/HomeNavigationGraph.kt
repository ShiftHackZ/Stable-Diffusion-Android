package com.shifthackz.aisdv1.presentation.screen.home

import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.get
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryScreen
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageScreen
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shz.imagepicker.imagepicker.ImagePickerCallback
import org.koin.androidx.compose.koinViewModel
import java.io.File

fun NavGraphBuilder.homeScreenNavGraph(
    route: String = Constants.ROUTE_HOME,
    pickImage: (ImagePickerCallback) -> Unit = {},
    takePhoto: (ImagePickerCallback) -> Unit = {},
    shareGalleryFile: (File) -> Unit = {},
    openGalleryItemDetails: (Long) -> Unit = {},
    launchIntent: (Intent) -> Unit = {},
    launchSetup: () -> Unit = {},
    launchUrl: (String) -> Unit = {},
    launchDebugMenu: () -> Unit = {},
    shareLogFile: () -> Unit = {},
    requestStoragePermissions: () -> Unit = {},
) {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            HomeNavigationScreen(
                viewModel = koinViewModel(),
                navItems = listOf(
                    txt2ImgTab(
                        launchGalleryDetail = openGalleryItemDetails,
                    ),
                    img2imgTab(
                        pickImage = pickImage,
                        takePhoto = takePhoto,
                        launchGalleryDetail = openGalleryItemDetails,
                        launchServerSetup = launchSetup,
                    ),
                    galleryTab(
                        shareGalleryFile = shareGalleryFile,
                        openGalleryItemDetails = openGalleryItemDetails,
                        launchIntent = launchIntent,
                    ),
                    settingsTab(
                        launchSetup = launchSetup,
                        launchUrl = launchUrl,
                        launchDebugMenu = launchDebugMenu,
                        shareLogFile = shareLogFile,
                        requestStoragePermissions = requestStoragePermissions,
                    ),
                ),
            ).Build()
        }.apply { this.route = route }
    )
}

@Composable
private fun txt2ImgTab(
    launchGalleryDetail: (Long) -> Unit,
) = HomeNavigationItem(
    name = stringResource(R.string.home_tab_txt_to_img),
    route = Constants.ROUTE_TXT_TO_IMG,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_text,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        TextToImageScreen(
            viewModel = koinViewModel(),
            launchGalleryDetail = launchGalleryDetail,
        ).Build()
    },
)

@Composable
private fun img2imgTab(
    pickImage: (ImagePickerCallback) -> Unit = {},
    takePhoto: (ImagePickerCallback) -> Unit = {},
    launchGalleryDetail: (Long) -> Unit,
    launchServerSetup: () -> Unit,
) = HomeNavigationItem(
    name = stringResource(R.string.home_tab_img_to_img),
    route = Constants.ROUTE_IMG_TO_IMG,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_image,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        ImageToImageScreen(
            viewModel = koinViewModel(),
            pickImage = pickImage,
            takePhoto = takePhoto,
            launchGalleryDetail = launchGalleryDetail,
            launchServerSetup = launchServerSetup,
        ).Build()
    },
)

@Composable
private fun galleryTab(
    shareGalleryFile: (File) -> Unit = {},
    openGalleryItemDetails: (Long) -> Unit = {},
    launchIntent: (Intent) -> Unit = {},
) = HomeNavigationItem(
    name = stringResource(R.string.home_tab_gallery),
    route = Constants.ROUTE_GALLERY,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_gallery,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        GalleryScreen(
            viewModel = koinViewModel(),
            shareGalleryFile = shareGalleryFile,
            openGalleryItemDetails = openGalleryItemDetails,
            launchIntent = launchIntent,
        ).Build()
    },
)

@Composable
private fun settingsTab(
    launchSetup: () -> Unit = {},
    launchUrl: (String) -> Unit = {},
    launchDebugMenu: () -> Unit = {},
    shareLogFile: () -> Unit = {},
    requestStoragePermissions: () -> Unit = {},
) = HomeNavigationItem(
    stringResource(id = R.string.home_tab_settings),
    Constants.ROUTE_SETTINGS,
    HomeNavigationItem.Icon.Vector(
        vector = Icons.Default.Settings,
    ),
    content = {
        SettingsScreen(
            viewModel = koinViewModel(),
            launchSetup = launchSetup,
            launchUrl = launchUrl,
            launchDebugMenu = launchDebugMenu,
            shareLogFile = shareLogFile,
            requestStoragePermissions = requestStoragePermissions,
        ).Build()
    }
)
