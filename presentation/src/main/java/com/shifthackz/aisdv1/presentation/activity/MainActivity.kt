package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationItem
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationScreen
import com.shifthackz.aisdv1.presentation.screen.splash.SplashLoaderScreen
import com.shifthackz.aisdv1.presentation.screen.txt2img.TextToImageScreen
import com.shifthackz.aisdv1.presentation.utils.Constants
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
                                name = stringResource(R.string.home_tab_txt_to_img),
                                route = Constants.ROUTE_TXT_TO_IMG,
                                icon = HomeNavigationItem.Icon.Resource(
                                    resId = R.drawable.ic_text,
                                    modifier = Modifier.size(24.dp),
                                ),
                                content = {
                                    TextToImageScreen(viewModel = koinViewModel()).Build()
                                },
                            ),
                            HomeNavigationItem(
                                name = stringResource(R.string.home_tab_img_to_img),
                                route = Constants.ROUTE_IMG_TO_IMG,
                                icon = HomeNavigationItem.Icon.Resource(
                                    resId = R.drawable.ic_image,
                                    modifier = Modifier.size(24.dp),
                                ),
//                                icon = HomeNavigationItem.Icon.Vector(Icons.Filled.Home),
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
