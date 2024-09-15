package com.shifthackz.aisdv1.presentation.navigation.graph

import androidx.navigation.NavArgument
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.get
import androidx.navigation.toRoute
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuScreen
import com.shifthackz.aisdv1.presentation.screen.donate.DonateScreen
import com.shifthackz.aisdv1.presentation.screen.gallery.detail.GalleryDetailScreen
import com.shifthackz.aisdv1.presentation.screen.inpaint.InPaintScreen
import com.shifthackz.aisdv1.presentation.screen.loader.ConfigurationLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.logger.LoggerScreen
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingScreen
import com.shifthackz.aisdv1.presentation.screen.onboarding.OnBoardingViewModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreen
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupViewModel
import com.shifthackz.aisdv1.presentation.screen.splash.SplashScreen
import com.shifthackz.aisdv1.presentation.screen.web.webui.WebUiScreen
import com.shifthackz.aisdv1.presentation.utils.Constants
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf
import kotlin.reflect.typeOf

fun NavGraphBuilder.mainNavGraph() {
    composable<NavigationRoute.Splash> {
        SplashScreen()
    }
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) { entry ->
            val sourceKey = entry.arguments
                ?.getInt(Constants.PARAM_SOURCE)
                ?: LaunchSource.SPLASH.ordinal
            ServerSetupScreen(
                viewModel = koinViewModel<ServerSetupViewModel>(
                    parameters = { parametersOf(sourceKey) }
                ),
                buildInfoProvider = koinInject()
            )
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
            LoggerScreen()
        }.apply {
            route = Constants.ROUTE_LOGGER
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            InPaintScreen()
        }.apply {
            route = Constants.ROUTE_IN_PAINT
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            WebUiScreen()
        }.apply {
            route = Constants.ROUTE_WEB_UI
        }
    )
    addDestination(
        ComposeNavigator.Destination(provider[ComposeNavigator::class]) {
            DonateScreen()
        }.apply {
            route = Constants.ROUTE_DONATE
        }
    )
    composable<NavigationRoute.Onboarding>(
        typeMap = mapOf(
            typeOf<LaunchSource>() to NavType.EnumType(LaunchSource::class.java)
        )
    ) { entry ->
        val sourceKey = entry.toRoute<NavigationRoute.Onboarding>().source.ordinal
        OnBoardingScreen(
            viewModel = koinViewModel<OnBoardingViewModel>(
                parameters = { parametersOf(sourceKey) }
            ),
        )
    }
}
