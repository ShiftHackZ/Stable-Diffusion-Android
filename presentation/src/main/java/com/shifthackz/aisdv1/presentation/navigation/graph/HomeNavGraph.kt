package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.navigation.router.home.HomeRouter
import com.shifthackz.aisdv1.presentation.screen.gallery.list.GalleryScreen
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.img2img.ImageToImageScreen
import com.shifthackz.aisdv1.presentation.screen.settings.SettingsScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import org.koin.compose.koinInject
import com.shifthackz.aisdv1.core.localization.R as LocalizationR
import com.shifthackz.aisdv1.presentation.R as PresentationR

fun NavGraphBuilder.homeScreenNavGraph() {
    composable<NavigationRoute.Home> {
        HomeNavigationScreen(
            navItems = listOf(
                txt2ImgTab(),
                img2imgTab(),
                galleryTab(),
                settingsTab(),
            ),
        )
    }
}

fun txt2ImgTab() = NavItem(
    name = LocalizationR.string.home_tab_txt_to_img.asUiText(),
    navRoute = NavigationRoute.HomeNavigation.TxtToImg,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_text,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(NavigationRoute.HomeNavigation.TxtToImg) {
            TextToImageScreen()
        }
    },
)

fun img2imgTab() = NavItem(
    name = LocalizationR.string.home_tab_img_to_img.asUiText(),
    navRoute = NavigationRoute.HomeNavigation.ImgToImg,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_image,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(NavigationRoute.HomeNavigation.ImgToImg) {
            ImageToImageScreen()
        }
    },
)

fun galleryTab() = NavItem(
    name = LocalizationR.string.home_tab_gallery.asUiText(),
    navRoute = NavigationRoute.HomeNavigation.Gallery,
    icon = NavItem.Icon.Resource(
        resId = PresentationR.drawable.ic_gallery,
        modifier = Modifier.size(24.dp),
    ),
    content = {
        HomeTabBase(NavigationRoute.HomeNavigation.Gallery) {
            GalleryScreen()
        }
    },
)

fun settingsTab() = NavItem(
    name = LocalizationR.string.home_tab_settings.asUiText(),
    navRoute = NavigationRoute.HomeNavigation.Settings,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Settings,
    ),
    content = {
        HomeTabBase(NavigationRoute.HomeNavigation.Settings) {
            SettingsScreen()
        }
    }
)

@Composable
private fun HomeTabBase(
    navRoute: NavigationRoute,
    content: @Composable () -> Unit,
) {
    val homeRouter: HomeRouter = koinInject()
    LaunchedEffect(Unit) {
        homeRouter.updateExternallyWithoutNavigation(navRoute = navRoute)
    }
    content()
}
