package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.model.NavItem
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityComposable
import com.shifthackz.aisdv1.presentation.widget.item.NavigationItemIcon
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeNavigationScreen(
    navItems: List<NavItem> = emptyList(),
) {
    require(navItems.isNotEmpty()) { "navItems collection must not be empty." }

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

    MviComponent(
        viewModel = koinViewModel<HomeNavigationViewModel>(),
        processEffect = { effect -> navigate(effect.route) },
    ) { _, _ ->
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
                                icon = { NavigationItemIcon(item.icon) },
                                onClick = { navigate(item.route) },
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
                                item.content?.invoke()
                            }
                        }
                    }
                }
            }
        )
    }
}
