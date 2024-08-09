package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.get
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryScreen
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageScreen
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import com.shifthackz.aisdv1.presentation.utils.Constants
import org.koin.compose.koinInject
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

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

fun txt2ImgTab() = NavItem(
    name = LocalizationR.string.home_tab_txt_to_img.asUiText(),
    route = Constants.ROUTE_TXT_TO_IMG,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_text,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(Constants.ROUTE_TXT_TO_IMG) {
            TextToImageScreen()
        }
    },
)

fun img2imgTab() = NavItem(
    name = LocalizationR.string.home_tab_img_to_img.asUiText(),
    route = Constants.ROUTE_IMG_TO_IMG,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_image,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(Constants.ROUTE_IMG_TO_IMG) {
            ImageToImageScreen()
        }
    },
)

fun galleryTab() = NavItem(
    name = LocalizationR.string.home_tab_gallery.asUiText(),
    route = Constants.ROUTE_GALLERY,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_gallery,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(Constants.ROUTE_GALLERY) {
            GalleryScreen()
        }
    },
)

fun settingsTab() = NavItem(
    name = LocalizationR.string.home_tab_settings.asUiText(),
    route = Constants.ROUTE_SETTINGS,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Settings,
    ),
    content = {
        HomeTabBase(Constants.ROUTE_SETTINGS) {
            SettingsScreen()
        }
    }
)

@Composable
private fun HomeTabBase(
    route: String,
    content: @Composable () -> Unit,
) {
    val homeRouter: HomeRouter = koinInject()
    LaunchedEffect(Unit) {
        homeRouter.updateExternallyWithoutNavigation(route)
    }
    content()
}
