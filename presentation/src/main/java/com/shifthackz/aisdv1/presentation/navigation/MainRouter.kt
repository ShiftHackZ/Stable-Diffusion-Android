package com.shifthackz.aisdv1.presentation.navigation

import androidx.navigation.NavHostController
import com.shifthackz.aisdv1.presentation.screen.debug.DebugMenuAccessor
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupLaunchSource
import com.shifthackz.aisdv1.presentation.utils.Constants

class MainRouter(
    private val navController: NavHostController,
    private val debugMenuAccessor: DebugMenuAccessor,
) : Router {

    override fun navigateBack() {
        navController.navigateUp()
    }

    override fun navigateToPostSplashConfigLoader() {
        navController.navigate(Constants.ROUTE_CONFIG_LOADER) {
            popUpTo(Constants.ROUTE_SPLASH) {
                inclusive = true
            }
        }
    }

    override fun navigateToHomeScreen() {
        navController.navigate(Constants.ROUTE_HOME) {
            navController.currentBackStackEntry?.destination?.route?.let {
                popUpTo(it) { inclusive = true }
            }
        }
    }

    override fun navigateToServerSetup(source: ServerSetupLaunchSource) {
        navController.navigate(
            "${Constants.ROUTE_SERVER_SETUP}/${source.key}"
        ) {
            if (source == ServerSetupLaunchSource.SPLASH) {
                popUpTo(Constants.ROUTE_SPLASH) {
                    inclusive = true
                }
            }
        }
    }

    override fun navigateToGalleryDetails(itemId: Long) {
        navController.navigate("${Constants.ROUTE_GALLERY_DETAIL}/$itemId")
    }

    override fun navigateToDebugMenu() {
        if (debugMenuAccessor.invoke()) {
            navController.navigate(Constants.ROUTE_DEBUG)
        }
    }
}
