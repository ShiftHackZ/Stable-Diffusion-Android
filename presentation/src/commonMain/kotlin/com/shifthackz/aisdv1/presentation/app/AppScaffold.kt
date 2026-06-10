@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.displayString
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.Settings
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.aisdv1.presentation.model.LaunchSource
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerSheetContent
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerSheetItem
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationBar
import com.shifthackz.aisdv1.presentation.screen.home.HomeNavigationBarItem
import com.shifthackz.aisdv1.presentation.widget.connectivity.ConnectivityComposable
import com.shifthackz.aisdv1.presentation.widget.source.getName

@Composable
internal fun AppScaffold(
    currentRoute: AppRoute,
    router: RootAppRouter,
    buildInfoProvider: BuildInfoProvider,
    preferenceManager: PreferenceManager,
    content: @Composable (Modifier) -> Unit,
) {
    val drawerOpen by router.drawerOpen.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val settings by preferenceManager.observe().collectAsState(Settings())
    val showHomeNavigation = currentRoute.isHomeRoute()

    LaunchedEffect(drawerOpen) {
        when {
            drawerOpen && drawerState.isClosed -> drawerState.open()
            !drawerOpen && drawerState.isOpen -> drawerState.close()
        }
    }

    LaunchedEffect(drawerState) {
        snapshotFlow { drawerState.isOpen }
            .collect { isOpen ->
                if (!isOpen) router.closeDrawer()
            }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen || showHomeNavigation,
        drawerContent = {
            DrawerSheetContent(
                items = appDrawerItems(
                    currentRoute = currentRoute,
                    settings = settings,
                    router = router,
                ),
                header = {
                    DrawerHeader(buildInfoProvider = buildInfoProvider)
                },
            )
        },
    ) {
        Scaffold(
            bottomBar = {
                if (showHomeNavigation) {
                    HomeNavigationBar(
                        items = appBottomNavigationItems(
                            currentRoute = currentRoute,
                            router = router,
                        ),
                    )
                }
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
            ) {
                if (showHomeNavigation) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center,
                    ) {
                        ConnectivityComposable()
                    }
                }
                content(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
private fun DrawerHeader(
    buildInfoProvider: BuildInfoProvider,
) {
    Row(
        modifier = Modifier.padding(horizontal = 28.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        DrawerBrandLogo(modifier = Modifier.size(56.dp))
        Column {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "SDAI",
                    style = MaterialTheme.typography.headlineMedium,
                )
                DrawerPlatformIcon(modifier = Modifier.size(18.dp))
            }
            Text(
                text = buildInfoProvider.displayString(),
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun appBottomNavigationItems(
    currentRoute: AppRoute,
    router: RootAppRouter,
): List<HomeNavigationBarItem> = listOf(
    HomeNavigationBarItem(
        label = Localization.string("home_tab_txt_to_img"),
        selected = currentRoute == AppRoute.Home || currentRoute == AppRoute.TextToImage,
        icon = { Icon(Icons.Default.TextFields, contentDescription = null) },
        onClick = router::navigateToTextToImage,
    ),
    HomeNavigationBarItem(
        label = Localization.string("home_tab_img_to_img"),
        selected = currentRoute == AppRoute.ImageToImage,
        icon = { Icon(Icons.Default.Image, contentDescription = null) },
        onClick = router::navigateToImageToImage,
    ),
    HomeNavigationBarItem(
        label = Localization.string("home_tab_gallery"),
        selected = currentRoute == AppRoute.Gallery,
        icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
        onClick = router::navigateToGallery,
    ),
    HomeNavigationBarItem(
        label = Localization.string("home_tab_settings"),
        selected = currentRoute == AppRoute.Settings,
        icon = { Icon(Icons.Default.Settings, contentDescription = null) },
        onClick = router::navigateToSettings,
    ),
)

@Composable
private fun appDrawerItems(
    currentRoute: AppRoute,
    settings: Settings,
    router: RootAppRouter,
): List<DrawerSheetItem> {
    val sourceName = settings.source.getName()
    return buildList {
        add(
            DrawerSheetItem(
                label = Localization.string("title_text_to_image"),
                selected = currentRoute == AppRoute.Home || currentRoute == AppRoute.TextToImage,
                icon = { Icon(Icons.Default.TextFields, contentDescription = null) },
                onClick = { router.navigateToTextToImage() },
            ),
        )
        add(
            DrawerSheetItem(
                label = Localization.string("title_image_to_image"),
                selected = currentRoute == AppRoute.ImageToImage,
                icon = { Icon(Icons.Default.Image, contentDescription = null) },
                onClick = { router.navigateToImageToImage() },
            ),
        )
        add(
            DrawerSheetItem(
                label = Localization.string("title_gallery"),
                selected = currentRoute == AppRoute.Gallery || currentRoute is AppRoute.GalleryDetail,
                icon = { Icon(Icons.Default.PhotoLibrary, contentDescription = null) },
                onClick = { router.navigateToGallery() },
            ),
        )
        if (settings.source.featureTags.contains(FeatureTag.OwnServer)) {
            add(
                DrawerSheetItem(
                    label = "${Localization.string("drawer_web_ui")} ($sourceName)",
                    selected = currentRoute == AppRoute.WebUi,
                    icon = { Icon(Icons.Default.Web, contentDescription = null) },
                    onClick = { router.navigateToWebUi() },
                ),
            )
        }
        add(
            DrawerSheetItem(
                label = Localization.string("title_settings"),
                selected = currentRoute == AppRoute.Settings,
                icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                onClick = { router.navigateToSettings() },
            ),
        )
        add(
            DrawerSheetItem(
                label = Localization.string("settings_item_config"),
                selected = currentRoute is AppRoute.Setup,
                icon = { Icon(Icons.Default.SettingsEthernet, contentDescription = null) },
                onClick = { router.navigateToServerSetup(LaunchSource.SETTINGS) },
            ),
        )
        if (settings.developerMode) {
            add(
                DrawerSheetItem(
                    label = Localization.string("title_debug_menu"),
                    selected = currentRoute == AppRoute.Debug,
                    icon = { Icon(Icons.Default.DeveloperMode, contentDescription = null) },
                    onClick = { router.navigateToDebugMenu() },
                ),
            )
        }
    }
}
