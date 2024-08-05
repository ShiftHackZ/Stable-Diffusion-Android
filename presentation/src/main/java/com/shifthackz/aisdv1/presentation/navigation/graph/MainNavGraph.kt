package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Deck
import androidx.compose.material.icons.filled.Home
import androidx.compose.runtime.Composable
import androidx.navigation.NavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.get
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintScreen
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreen
import com.shifthackz.aisdv1.presentation.screen.splash.SplashScreen
import com.shifthackz.aisdv1.presentation.utils.Constants

fun NavGraphBuilder.mainNavGraph() {
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            SplashScreen()
        }.apply {
            route = Constants.ROUTE_SPLASH
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) { entry ->
            val sourceKey = entry.arguments
                ?.getInt(Constants.PARAM_SOURCE)
                ?: ServerSetupLaunchSource.SPLASH.ordinal
            ServerSetupScreen(launchSourceKey = sourceKey)
        }.apply {
            route = Constants.ROUTE_SERVER_SETUP_FULL
            addArgument(
                Constants.PARAM_SOURCE,
                NavArgument.Builder().setType(NavType.IntType).build(),
            )
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            ConfigurationLoaderScreen()
        }.apply {
            route = Constants.ROUTE_CONFIG_LOADER
        }
    )
    homeScreenNavGraph(Constants.ROUTE_HOME)
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) { entry ->
            val itemId = entry.arguments?.getLong(Constants.PARAM_ITEM_ID) ?: -1L
            GalleryDetailScreen(itemId = itemId)
        }.apply {
            route = Constants.ROUTE_GALLERY_DETAIL_FULL
            addArgument(
                Constants.PARAM_ITEM_ID,
                NavArgument.Builder().setType(NavType.LongType).build(),
            )
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            DebugMenuScreen()
        }.apply {
            route = Constants.ROUTE_DEBUG
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            InPaintScreen()
        }.apply {
            route = Constants.ROUTE_IN_PAINT
        }
    )
}

@Composable
fun mainDrawerNavItems(): List<NavItem> = listOf(
    homeScreenTab(),
    debugMenuTab(),
)

@Composable
private fun homeScreenTab() = NavItem(
    name = "Home",
    route = Constants.ROUTE_HOME,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Home,
    ),
)

@Composable
private fun debugMenuTab() = NavItem(
    name = "Debug Menu",
    route = Constants.ROUTE_DEBUG,
    icon = NavItem.Icon.Vector(
        vector = Icons.Default.Deck,
    ),
)
