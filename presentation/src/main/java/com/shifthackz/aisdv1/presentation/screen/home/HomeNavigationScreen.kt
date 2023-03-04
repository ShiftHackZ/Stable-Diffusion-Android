@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.home

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.ui.Screen

class HomeNavigationScreen(
    private val navItems: List<HomeNavigationItem> = emptyList(),
) : Screen() {

    override val statusBarColor: Color = Color.Cyan
    override val navigationBarColor: Color = Color.Black

    @Composable
    override fun Content() {
        require(navItems.isNotEmpty()) { "navItems collection must not be empty." }
        val navController = rememberNavController()
        val backStackEntry = navController.currentBackStackEntryAsState()
        Scaffold(
            bottomBar = {
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
                                Icon(
                                    imageVector = item.icon,
                                    contentDescription = item.name,
                                )
                            },
                            onClick = {
                                //if (selected) return@NavigationBarItem
                                navController.navigate(item.route) {
                                    navController.graph.startDestinationRoute?.let { route ->
                                        popUpTo(route) {
                                            saveState = true
                                        }
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                        )
                    }
                }
            },
            content = { paddingValues ->
                NavHost(
                    modifier = Modifier.padding(paddingValues),
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
        )
    }
}
