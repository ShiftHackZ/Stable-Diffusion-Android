@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.ui.Screen
import com.shifthackz.aisdv1.domain.feature.ad.AdFeature
import com.shifthackz.aisdv1.presentation.widget.ad.AdMobBanner
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityComposable
import org.koin.androidx.compose.koinViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class HomeNavigationScreen(
    private val viewModel: HomeNavigationViewModel,
    private val navItems: List<HomeNavigationItem> = emptyList(),
) : Screen(), KoinComponent {

    private val adFeature: AdFeature by inject()

    @Composable
    override fun Content() {
        require(navItems.isNotEmpty()) { "navItems collection must not be empty." }
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState()
        Scaffold(
            bottomBar = {
                Column {
                    AdMobBanner(
                        modifier =  Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        adFeature = adFeature,
                        adFactory = adFeature::getHomeScreenBannerAd,
                    )
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
                                    navController.navigate(item.route) {
                                        navController.graph.startDestinationRoute?.let { route ->
                                            popUpTo(route) {
                                                saveState = true
                                            }
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
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
    }
}
