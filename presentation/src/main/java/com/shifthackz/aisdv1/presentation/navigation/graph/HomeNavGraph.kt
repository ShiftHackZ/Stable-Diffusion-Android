package com.shifthackz.aisdv1.presentation.navigation.graph

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
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationItem
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageScreen
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import com.shifthackz.aisdv1.presentation.utils.Constants

fun NavGraphBuilder.homeScreenNavGraph(route: String = Constants.ROUTE_HOME) {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            HomeNavigationScreen(
                navItems = listOf(
                    txt2ImgTab(),
                    img2imgTab(),
                    galleryTab(),
                    settingsTab(),
                ),
            )
        }.apply { this.route = route }
    )
}

@Composable
private fun txt2ImgTab() = HomeNavigationItem(
    name = stringResource(R.string.home_tab_txt_to_img),
    route = Constants.ROUTE_TXT_TO_IMG,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_text,
        modifier = Modifier.size(24.dp),
    ),
    content = { TextToImageScreen() },
)

@Composable
private fun img2imgTab() = HomeNavigationItem(
    name = stringResource(R.string.home_tab_img_to_img),
    route = Constants.ROUTE_IMG_TO_IMG,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_image,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        ImageToImageScreen()
    },
)

@Composable
private fun galleryTab() = HomeNavigationItem(
    name = stringResource(R.string.home_tab_gallery),
    route = Constants.ROUTE_GALLERY,
    icon = HomeNavigationItem.Icon.Resource(
        resId = R.drawable.ic_gallery,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        GalleryScreen()
    },
)

@Composable
private fun settingsTab() = HomeNavigationItem(
    name = stringResource(id = R.string.home_tab_settings),
    route = Constants.ROUTE_SETTINGS,
    icon = HomeNavigationItem.Icon.Vector(
        vector = Icons.Default.Settings,
    ),
    content = {
        SettingsScreen()
    }
)
