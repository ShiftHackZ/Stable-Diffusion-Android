package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                startDestination = "splash",
            ) {
                composable("splash") {
                    SplashLoaderScreen(
                        viewModel = koinViewModel(),
                        onNavigateNextScreen = {
                            navController.navigate("text_to_image") {
                                popUpTo("splash") {
                                    inclusive = true
                                }
                            }
                        }
                    ).Build()
                }

                composable("text_to_image") {
                    TextToImageScreen(viewModel = koinViewModel()).Build()
                }
            }
        }
    }
}
