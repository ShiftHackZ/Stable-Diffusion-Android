@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.FilePresent
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.di.initKoin
import com.shifthackz.aisdv1.presentation.modal.grid.GridBottomSheet
import com.shifthackz.aisdv1.presentation.modal.language.LanguageBottomSheet
import com.shifthackz.aisdv1.presentation.navigation.router.SettingsRouter
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.widget.color.AccentColorSelector
import com.shifthackz.aisdv1.presentation.widget.color.DarkThemeColorSelector
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.InfoDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.icon.BrandIcons
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.GridIcon
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItemContent
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import kotlinx.coroutines.launch
import org.koin.core.parameter.parametersOf

/**
 * Renders the `SettingsScreen` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param router router value consumed by the API.
 * @author Dmitriy Moroz
 */
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

/**
 * Renders the `SettingsScreenContent` UI for the SDAI presentation layer.
 *
 * @param modifier Compose modifier applied to the rendered UI.
 * @param state state rendered or processed by the component.
 * @param platformActions platform actions value consumed by the API.
 * @param processIntent process intent value consumed by the API.
 * @author Dmitriy Moroz
 */
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

