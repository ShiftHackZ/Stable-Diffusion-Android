package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.extensions.navigatePopUpToCurrent
import com.shifthackz.aisdv1.presentation.navigation.NavigationEffect
import com.shifthackz.aisdv1.presentation.navigation.graph.mainNavGraph
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerScreen
import com.shifthackz.aisdv1.presentation.theme.AiStableDiffusionAppTheme
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AiStableDiffusionActivity : ComponentActivity() {

    private val viewModel: AiStableDiffusionViewModel by viewModel()

    private val notificationPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        debugLog("Notification permission is ${if (granted) "GRANTED" else "DENIED"}.")
    }

    private val storagePermission = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) viewModel.onStoragePermissionsGranted()
        debugLog("Storage permission is ${result}.")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        PermissionUtil.checkNotificationPermission(this, notificationPermission::launch)
        PermissionUtil.checkStoragePermission(this, storagePermission::launch)
        setContent {
            val navController = rememberNavController()
            val backStackEntry = navController.currentBackStackEntryAsState()
            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            AiStableDiffusionAppTheme {
                MviComponent(
                    viewModel = viewModel,
                    processEffect = { effect ->
                        when (effect) {
                            NavigationEffect.Back -> navController.navigateUp()

                            is NavigationEffect.Navigate.Route -> {
                                navController.navigate(effect.route)
                            }

                            is NavigationEffect.Navigate.RouteBuilder -> navController.navigate(
                                effect.route, effect.builder,
                            )

                            is NavigationEffect.Navigate.RoutePopUp -> {
                                navController.navigatePopUpToCurrent(effect.route)
                            }

                            NavigationEffect.Drawer.Close -> scope.launch {
                                drawerState.close()
                            }

                            NavigationEffect.Drawer.Open -> scope.launch {
                                drawerState.open()
                            }
                        }
                    }
                ) { _, _ ->
                    DrawerScreen(
                        drawerState = drawerState,
                        backStackEntry = backStackEntry,
                        onNavigate = navController::navigate,
//                        navItems = mainDrawerNavItems(),
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = Constants.ROUTE_SPLASH,
                            builder = { mainNavGraph() },
                        )
                    }
                }
            }
        }
    }
}
