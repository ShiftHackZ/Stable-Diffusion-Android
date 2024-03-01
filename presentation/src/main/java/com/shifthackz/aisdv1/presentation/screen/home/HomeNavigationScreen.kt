package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityComposable
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeNavigationScreen(
    navItems: List<HomeNavigationItem> = emptyList(),
) {
    require(navItems.isNotEmpty()) { "navItems collection must not be empty." }

    val viewModel: HomeNavigationViewModel = koinViewModel()

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
                NavigationBar {
                    val currentRoute = backStackEntry.value?.destination?.route
                    navItems.forEach { item ->
                        val selected = item.route == currentRoute
                        NavigationBarItem(
                            selected = selected,
                            label = {
                                Text(
                                    text = item.name,
                                    color = LocalContentColor.current,
                                )
                            },
                            colors = NavigationBarItemDefaults.colors().copy(
                                selectedIndicatorColor = MaterialTheme.colorScheme.primary,
                            ),
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
                                        tint = LocalContentColor.current,
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
                ConnectivityComposable()
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
    LaunchedEffect(Unit) {
        viewModel.routeEffectStream.collect { route -> navigate(route) }
    }
}
