package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationItem
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.splash.SplashLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import org.koin.androidx.compose.koinViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Constants.ROUTE_SPLASH,
            ) {
                composable(Constants.ROUTE_SPLASH) {
                    SplashLoaderScreen(
                        viewModel = koinViewModel(),
                        onNavigateNextScreen = {
                            navController.navigate(Constants.ROUTE_HOME) {
                                popUpTo(Constants.ROUTE_SPLASH) {
                                    inclusive = true
                                }
                            }
                        }
                    ).Build()
                }

                composable(Constants.ROUTE_HOME) {
                    HomeNavigationScreen(
                        navItems = listOf(
                            HomeNavigationItem(
                                name = "Txt 2 Img",
                                route = Constants.ROUTE_TXT_TO_IMG,
                                icon = Icons.Filled.Home,
                                content = {
                                    TextToImageScreen(viewModel = koinViewModel()).Build()
                                },
                            ),
                            HomeNavigationItem(
                                name = "Img 2 Img",
                                route = Constants.ROUTE_IMG_TO_IMG,
                                icon = Icons.Filled.Home,
                                content = {
                                    Text("Not implemented")
                                },
                            ),
                        ),
                    ).Build()
                }
            }
        }
    }
}
