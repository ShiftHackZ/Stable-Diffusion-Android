package com.shifthackz.aisdv1.presentation.activity

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.presentation.extensions.navigatePopUpToCurrent
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.NavigationRoute
import com.shifthackz.aisdv1.presentation.navigation.graph.mainNavGraph
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerScreen
import com.shifthackz.aisdv1.presentation.theme.global.AiSdAppTheme
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.android.core.mvi.MviComponent
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AiStableDiffusionActivity : AppCompatActivity() {

    private val viewModel: AiStableDiffusionViewModel by viewModel()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        debugLog("Notification permission is ${if (granted) "GRANTED" else "DENIED"}.")
    }

    private val storagePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) {
            viewModel.processIntent(AppIntent.GrantStoragePermission)
        }
        debugLog("Storage permission is ${result}.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        splashScreen.setKeepOnScreenCondition { viewModel.state.value.isShowSplash }
        splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
            val fadeOutAnimation = ObjectAnimator.ofFloat(
                splashScreenViewProvider.view,
                View.ALPHA,
                1f,
                0f
            )
            fadeOutAnimation.duration = 500L
            fadeOutAnimation.doOnEnd {
                PermissionUtil.checkNotificationPermission(this, notificationPermission::launch)
                PermissionUtil.checkStoragePermission(this, storagePermission::launch)
                splashScreenViewProvider.remove()
            }
            fadeOutAnimation.start()
        }
        setContent {
            val navController = rememberNavController()
            val backStackEntry by navController.currentBackStackEntryAsState()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            var homeRouteEntry: NavigationRoute? by remember { mutableStateOf(null) }

            BackHandler(enabled = drawerState.isOpen) {
                scope.launch { drawerState.close() }
            }

            LaunchedEffect(backStackEntry) {
                if (!viewModel.state.value.isShowSplash) return@LaunchedEffect
                backStackEntry?.let { entry ->
                    if (entry.destination.hasRoute(NavigationRoute.Splash::class)) return@LaunchedEffect
                    viewModel.processIntent(AppIntent.HideSplash)
                }
            }

            AiSdAppTheme {
                MviComponent(
                    viewModel = viewModel,
                    processEffect = { effect ->
                        when (effect) {
                            NavigationEffect.Back -> navController.navigateUp()

                            is NavigationEffect.Navigate.Route -> {
                                navController.navigate(effect.navRoute)
                            }

                            is NavigationEffect.Navigate.RouteBuilder -> {
                                navController.navigate(
                                    effect.navRoute, effect.builder,
                                )
                            }

                            is NavigationEffect.Navigate.RoutePopUp -> {
                                navController.navigatePopUpToCurrent(effect.navRoute)
                            }

                            NavigationEffect.Drawer.Close -> scope.launch {
                                drawerState.close()
                            }

                            NavigationEffect.Drawer.Open -> scope.launch {
                                drawerState.open()
                            }

                            is NavigationEffect.Home -> {
                                homeRouteEntry = effect.navRoute
                            }
                        }
                    }
                ) { state ->
                    DrawerScreen(
                        drawerState = drawerState,
                        backStackEntry = backStackEntry,
                        homeRouteEntry = homeRouteEntry,
                        onRootNavigate = navController::navigate,
                        onHomeNavigate = { viewModel.processIntent(AppIntent.HomeRoute(it)) },
                        navItems = state.drawerItems,
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = NavigationRoute.Splash,
                            builder = { mainNavGraph() },
                        )
                    }
                }
            }
        }
    }
}
