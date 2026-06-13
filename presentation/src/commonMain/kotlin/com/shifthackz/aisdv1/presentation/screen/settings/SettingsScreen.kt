@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.screen.settings.content.ContentSettingsState
import com.shifthackz.aisdv1.presentation.screen.settings.modal.SettingsModalRenderer
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsIntent
import com.shifthackz.aisdv1.presentation.screen.settings.model.SettingsState
import com.shifthackz.aisdv1.presentation.screen.settings.platform.NoOpSettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.platform.SettingsPlatformActions
import com.shifthackz.aisdv1.presentation.screen.settings.platform.rememberSettingsPlatformActions
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import org.koin.core.parameter.parametersOf

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    router: SettingsRouter? = null,
) {
    val koin = remember { initKoin() }
    val platformActions = rememberSettingsPlatformActions()
    val resolvedRouter = remember(koin, router) {
        router ?: koin.get<SettingsRouter>()
    }
    val viewModel = remember(
        koin,
        resolvedRouter,
        platformActions,
    ) {
        koin.get<SettingsViewModel> {
            parametersOf(resolvedRouter, platformActions)
        }
    }

    MviComponent(viewModel = viewModel) { state, intentHandler ->
        SettingsScreenContent(
            modifier = modifier,
            state = state,
            platformActions = platformActions,
            processIntent = intentHandler,
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    platformActions: SettingsPlatformActions = NoOpSettingsPlatformActions,
    processIntent: (SettingsIntent) -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        navigationIcon = {
                            IconButton(onClick = {
                                processIntent(SettingsIntent.Drawer(DrawerIntent.Open))
                            }) {
                                Icon(
                                    imageVector = Icons.Default.Menu,
                                    contentDescription = Localization.string("action_menu"),
                                )
                            }
                        },
                        title = {
                            Text(
                                text = Localization.string("title_settings"),
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        windowInsets = WindowInsets(0, 0, 0, 0),
                    )
                    BackgroundWorkWidget(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(vertical = 4.dp),
                    )
                }
            },
            content = { paddingValues ->
                ContentSettingsState(
                    modifier = Modifier
                        .padding(
                            horizontal = paddingValues.calculateStartPadding(
                                LocalLayoutDirection.current,
                            ),
                        )
                        .padding(top = paddingValues.calculateTopPadding()),
                    state = state,
                    platformActions = platformActions,
                    processIntent = processIntent,
                )
            },
        )
        SettingsModalRenderer(
            screenModal = state.screenModal,
            platformActions = platformActions,
            processIntent = processIntent,
        )
    }
}
