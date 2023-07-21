package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.presentation.widget.ad.AdMobBanner
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityComposable
import com.shifthackz.aisdv1.presentation.widget.motd.MotdComposable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeNavigationScreen(
    private val viewModel: HomeNavigationViewModel,
    private val navItems: List<HomeNavigationItem> = emptyList(),
) : MviScreen<HomeNavigationState, EmptyEffect>(viewModel), KoinComponent {

    private val adFeature: AdFeature by inject()

    @Composable
    override fun Content() {
        require(navItems.isNotEmpty()) { "navItems collection must not be empty." }
        val state = viewModel.state.collectAsStateWithLifecycle().value
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState()

        val navigate: (String) -> Unit = { route ->
            navController.navigate(route) {
                navController.graph.startDestinationRoute?.let { route ->
                    popUpTo(route) {
                        saveState = true
                    }
                }
                launchSingleTop = true
                restoreState = true
            }
        }

        Scaffold(
            bottomBar = {
                Column {
                    if (state.bottomAdBanner) {
                        AdMobBanner(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            adFeature = adFeature,
                            adFactory = adFeature::getHomeScreenBannerAd,
                        )
                    }
                    NavigationBar {
                        val currentRoute = backStackEntry.value?.destination?.route
                        navItems.forEach { item ->
                            val selected = item.route == currentRoute
                            NavigationBarItem(
                                selected = selected,
                                label = {
                                    Text(text = item.name)
                                },
                                icon = {
                                    when (item.icon) {
                                        is HomeNavigationItem.Icon.Resource -> Image(
                                            modifier = item.icon.modifier,
                                            painter = painterResource(item.icon.resId),
                                            contentDescription = "",
                                            colorFilter = ColorFilter.tint(LocalContentColor.current),
                                        )
                                        is HomeNavigationItem.Icon.Vector -> Icon(
                                            modifier = item.icon.modifier,
                                            imageVector = item.icon.vector,
                                            contentDescription = item.name,
                                        )
                                    }
                                },
                                onClick = {
                                    navigate(item.route)
                                    viewModel.logNavItemClickEvent(item)
                                },
                            )
                        }
                    }
                }
            },
            content = { paddingValues ->
                Column(Modifier.padding(paddingValues)) {
                    ConnectivityComposable(koinViewModel()).Build()
                    MotdComposable(koinViewModel()).Build()
                    NavHost(
                        modifier = Modifier.fillMaxSize(),
                        navController = navController,
                        startDestination = navItems.first().route,
                    ) {
                        navItems.forEach { item ->
                            composable(item.route) {
                                item.content()
                            }
                        }
                    }
                }

            }
        )
        LaunchedEffect(KEY_HOME_NAV_ROUTE_EFFECT_PROCESSOR) {
            viewModel.routeEffectStream.collect { route -> navigate(route) }
        }
    }

    companion object {
        private const val KEY_HOME_NAV_ROUTE_EFFECT_PROCESSOR = "home_nav_route_effect"
    }
}
