package com.shifthackz.aisdv1.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.shifthackz.aisdv1.core.common.log.debugLog
import com.shifthackz.aisdv1.domain.feature.analytics.Analytics
import com.shifthackz.aisdv1.presentation.navigation.MainRouter
import com.shifthackz.aisdv1.presentation.navigation.Router
import com.shifthackz.aisdv1.presentation.navigation.graph.mainNavGraph
import com.shifthackz.aisdv1.presentation.theme.AiStableDiffusionAppTheme
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

class AiStableDiffusionActivity : ComponentActivity() {

    private val viewModel: AiStableDiffusionViewModel by viewModel()
    private val analytics: Analytics by inject()

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
        analytics.initialize()
        PermissionUtil.checkNotificationPermission(this, notificationPermission::launch)
        PermissionUtil.checkStoragePermission(this, storagePermission::launch)
        setContent {
            val navController = rememberNavController()

            loadKoinModules(
                module = module {
                    single<Router> {
                        MainRouter(navController, get())
                    }
                }
            )

            AiStableDiffusionAppTheme {
                NavHost(
                    navController = navController,
                    startDestination = Constants.ROUTE_SPLASH,
                    builder = { mainNavGraph() },
                )
            }
        }
    }
}
