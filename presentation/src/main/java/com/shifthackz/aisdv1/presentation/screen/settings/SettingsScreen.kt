@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatColorFill
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MiscellaneousServices
import androidx.compose.material.icons.filled.MonetizationOn
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.common.extensions.showToast
import com.shifthackz.aisdv1.core.common.math.roundTo
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.android.core.mvi.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.screen.drawer.DrawerIntent
import com.shifthackz.aisdv1.presentation.theme.colorTokenPalette
import com.shifthackz.aisdv1.presentation.theme.isSdAppInDarkTheme
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.aisdv1.presentation.utils.ReportProblemEmailComposer
import com.shifthackz.aisdv1.presentation.widget.color.AccentColorSelector
import com.shifthackz.aisdv1.presentation.widget.color.DarkThemeColorSelector
import com.shifthackz.aisdv1.presentation.widget.item.GridIcon
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItemContent
import com.shifthackz.aisdv1.presentation.widget.work.BackgroundWorkWidget
import com.shifthackz.android.compose.daynightswitch.DayNightSwitch
import org.koin.androidx.compose.koinViewModel
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsViewModel>()
    val context = LocalContext.current

    val storagePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        viewModel.processIntent(SettingsIntent.Permission.Storage(!result.values.any { !it }))
    }

    val notificationPermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.processIntent(SettingsIntent.Permission.Notification(granted))
    }

    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                SettingsEffect.RequestPermission.Storage -> {
                    if (PermissionUtil.checkStoragePermission(context, storagePermission::launch)) {
                        viewModel.processIntent(SettingsIntent.Permission.Storage(true))
                    }
                }

                SettingsEffect.RequestPermission.Notifications -> {
                    if (PermissionUtil.checkNotificationPermission(
                            context,
                            notificationPermission::launch
                        )
                    ) {
                        viewModel.processIntent(SettingsIntent.Permission.Notification(true))
                    }
                }

                SettingsEffect.ShareLogFile -> ReportProblemEmailComposer().invoke(context)
                is SettingsEffect.OpenUrl -> context.openUrl(effect.url)
                SettingsEffect.DeveloperModeUnlocked -> context.showToast(
                    LocalizationR.string.debug_action_unlock,
                )
            }
        },
    ) { state, intentHandler ->
        SettingsScreenContent(
            state = state,
            processIntent = intentHandler,
        )
    }
}

@Composable
fun SettingsScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
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
                                    contentDescription = "Menu",
                                )
                            }
                        },
                        title = {
                            Text(
                                text = stringResource(id = LocalizationR.string.title_settings),
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
                        .padding(horizontal = 16.dp),
                    state = state,
                    processIntent = processIntent,
                )
            }
        )
        ModalRenderer(screenModal = state.screenModal) {
            (it as? SettingsIntent)?.let(processIntent::invoke)
        }
    }
}

@Composable
private fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState,
    processIntent: (SettingsIntent) -> Unit = {},
) {
    val isDark = isSdAppInDarkTheme()
    val palette = colorTokenPalette(
        isDark = isDark,
        darkThemeToken = state.darkThemeToken,
    )
    val scrollState = rememberScrollState()
    Column(
        modifier = modifier.verticalScroll(scrollState),
    ) {
        val headerModifier = Modifier.padding(top = 28.dp, bottom = 8.dp)

        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)

        val warningModifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, start = 4.dp)

        if (!state.onBoardingDemo) {
            //region MAIN SETTINGS
            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = LocalizationR.string.settings_header_server.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.SettingsEthernet,
                text = LocalizationR.string.settings_item_config.asUiText(),
                endValueText = when (state.serverSource) {
                    ServerSource.AUTOMATIC1111 -> LocalizationR.string.srv_type_own_short
                    ServerSource.HORDE -> LocalizationR.string.srv_type_horde_short
                    ServerSource.HUGGING_FACE -> LocalizationR.string.srv_type_hugging_face_short
                    ServerSource.OPEN_AI -> LocalizationR.string.srv_type_open_ai
                    ServerSource.STABILITY_AI -> LocalizationR.string.srv_type_stability_ai
                    ServerSource.LOCAL_MICROSOFT_ONNX -> LocalizationR.string.srv_type_local_short
                    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> LocalizationR.string.srv_type_media_pipe_short
                    ServerSource.SWARM_UI -> LocalizationR.string.srv_type_swarm_ui
                }.asUiText(),
                onClick = { processIntent(SettingsIntent.NavigateConfiguration) },
            )
            if (state.showStabilityAiCredits) SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                enabled = false,
                startIcon = Icons.Default.Circle,
                text = LocalizationR.string.settings_item_stability_ai_credits.asUiText(),
                endValueText = state.stabilityAiCredits.roundTo(4).toString().asUiText(),
            )
            if (state.showSdModelSelector) SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.AutoFixNormal,
                text = LocalizationR.string.settings_item_sd_model.asUiText(),
                endValueText = state.sdModelSelected.asUiText(),
                onClick = { processIntent(SettingsIntent.SdModel.OpenChooser) },
            )
            if (state.showLocalMicrosoftONNXUseNNAPI) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.AccountTree,
                    text = LocalizationR.string.settings_item_local_nnapi.asUiText(),
                    endValueText = state.sdModelSelected.asUiText(),
                    onClick = { processIntent(SettingsIntent.UpdateFlag.NNAPI(!state.localUseNNAPI)) },
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.localUseNNAPI,
                            onCheckedChange = { processIntent(SettingsIntent.UpdateFlag.NNAPI(it)) },
                        )
                    }
                )
                AnimatedVisibility(visible = !state.loading) {
                    Text(
                        modifier = warningModifier,
                        text = stringResource(id = LocalizationR.string.settings_item_local_nnapi_warning),
                        style = MaterialTheme.typography.labelMedium,
                    )
                }
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.MiscellaneousServices,
                text = LocalizationR.string.settings_item_background_generation.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.BackgroundGeneration(!state.backgroundGeneration))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.backgroundGeneration,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.BackgroundGeneration(it))
                        },
                    )
                },
            )
            AnimatedVisibility(visible = !state.loading) {
                Text(
                    modifier = warningModifier,
                    text = stringResource(id = LocalizationR.string.settings_item_background_generation_warning),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            AnimatedVisibility(visible = !state.loading && state.developerMode) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.DeveloperMode,
                    text = LocalizationR.string.title_debug_menu.asUiText(),
                    onClick = { processIntent(SettingsIntent.NavigateDeveloperMode) },
                )
            }
            //endregion

            //region APP SETTINGS
            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = LocalizationR.string.settings_header_app.asUiText(),
            )
            if (state.showMonitorConnectionOption) SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Refresh,
                text = LocalizationR.string.settings_item_monitor_connection.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.MonitorConnection(!state.monitorConnectivity))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.monitorConnectivity,
                        onCheckedChange = {
                            processIntent(
                                SettingsIntent.UpdateFlag.MonitorConnection(
                                    it
                                )
                            )
                        },
                    )
                },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Save,
                text = UiText.Concat(
                    LocalizationR.string.settings_item_auto_save.asUiText(),
                    if (state.backgroundGeneration) "*" else "",
                ),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.AutoSaveResult(!state.autoSaveAiResults))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.autoSaveAiResults,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.AutoSaveResult(it))
                        },
                    )
                },
            )
            AnimatedVisibility(
                visible = !state.loading && state.backgroundGeneration,
            ) {
                Text(
                    modifier = warningModifier,
                    text = stringResource(id = LocalizationR.string.settings_item_auto_save_warning),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Folder,
                text = LocalizationR.string.settings_item_auto_save_media_store.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(!state.saveToMediaStore))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.saveToMediaStore,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.SaveToMediaStore(it))
                        },
                    )
                },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Tag,
                text = LocalizationR.string.settings_item_tagged_input.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.TaggedInput(!state.formPromptTaggedInput))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.formPromptTaggedInput,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.TaggedInput(it))
                        },
                    )
                },
            )
            if (state.showFormAdvancedOption) {
                SettingsItem(
                    modifier = itemModifier,
                    loading = state.loading,
                    startIcon = Icons.Default.DynamicForm,
                    text = LocalizationR.string.settings_item_advanced_form_default.asUiText(),
                    onClick = {
                        processIntent(SettingsIntent.UpdateFlag.AdvancedFormVisibility(!state.formAdvancedOptionsAlwaysShow))
                    },
                    endValueContent = {
                        Switch(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            checked = state.formAdvancedOptionsAlwaysShow,
                            onCheckedChange = {
                                processIntent(SettingsIntent.UpdateFlag.AdvancedFormVisibility(it))
                            },
                        )
                    },
                )
            }
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.DeleteForever,
                text = LocalizationR.string.settings_item_clear_cache.asUiText(),
                onClick = { processIntent(SettingsIntent.Action.ClearAppCache.Request) },
            )
            //endregion
        }

        //region LOOK AND FEEL
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = LocalizationR.string.settings_header_look_and_feel.asUiText(),
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Translate,
            text = LocalizationR.string.settings_item_lf_lang.asUiText(),
            endValueText = LocalizationR.string.language.asUiText(),
            onClick = { processIntent(SettingsIntent.Action.PickLanguage) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            text = LocalizationR.string.settings_item_lf_gallery_grid.asUiText(),
            endValueText = state.galleryGrid.size.toString().asUiText(),
            startIconContent = {
                GridIcon(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    grid = state.galleryGrid,
                    color = LocalContentColor.current,
                )
            },
            onClick = { processIntent(SettingsIntent.Action.GalleryGrid.Pick) },
        )
        if (state.showUseSystemColorPalette) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Save,
                text = LocalizationR.string.settings_item_lf_dynamic_colors.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.DynamicColors(!state.useSystemColorPalette))
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.useSystemColorPalette,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.DynamicColors(it))
                        },
                    )
                },
            )
        }
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.InvertColors,
            text = LocalizationR.string.settings_item_lf_system_dark_theme.asUiText(),
            onClick = {
                processIntent(SettingsIntent.UpdateFlag.SystemDarkTheme(!state.useSystemDarkTheme))
            },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.useSystemDarkTheme,
                    onCheckedChange = {
                        processIntent(SettingsIntent.UpdateFlag.SystemDarkTheme(it))
                    },
                )
            },
        )
        AnimatedVisibility(visible = !state.useSystemDarkTheme) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.DarkMode,
                text = LocalizationR.string.settings_item_lf_dark_theme.asUiText(),
                onClick = {
                    processIntent(SettingsIntent.UpdateFlag.DarkTheme(!state.darkTheme))
                },
                endValueContent = {
                    DayNightSwitch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.darkTheme,
                        animationDurationMillis = 225,
                        onCheckedChange = {
                            processIntent(SettingsIntent.UpdateFlag.DarkTheme(it))
                        },
                    )
                },
            )
        }
        AnimatedVisibility(visible = !state.loading && isDark) {
            Column(
                modifier = itemModifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItemContent(
                    text = LocalizationR.string.settings_item_lf_dark_theme_color.asUiText(),
                    icon = Icons.Default.FormatColorFill,
                )
                Spacer(modifier = Modifier.height(16.dp))
                DarkThemeColorSelector(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    selectedToken = state.darkThemeToken,
                    colorToken = state.colorToken,
                    onSelected = { _, token ->
                        processIntent(SettingsIntent.NewDarkThemeToken(token))
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        AnimatedVisibility(visible = !state.loading) {
            Column(
                modifier = itemModifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItemContent(
                    text = LocalizationR.string.settings_item_lf_accent_color.asUiText(),
                    icon = Icons.Default.ColorLens,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AccentColorSelector(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    palette = palette,
                    selectedToken = state.colorToken,
                    onSelected = { _, token ->
                        processIntent(SettingsIntent.NewColorToken(token))
                    },
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        //endregion

        if (!state.onBoardingDemo) {
            //region LINKS & OTHERS
            SettingsHeader(
                modifier = headerModifier,
                loading = state.loading,
                text = LocalizationR.string.settings_header_info.asUiText(),
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.MonetizationOn,
                text = LocalizationR.string.settings_item_donate.asUiText(),
                onClick = { processIntent(SettingsIntent.Action.Donate) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.AllInclusive,
                text = LocalizationR.string.settings_item_on_boarding.asUiText(),
                onClick = { processIntent(SettingsIntent.Action.OnBoarding) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Report,
                text = LocalizationR.string.settings_item_report_problem.asUiText(),
                onClick = { processIntent(SettingsIntent.Action.ReportProblem) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Gavel,
                text = LocalizationR.string.settings_item_policy.asUiText(),
                onClick = { processIntent(SettingsIntent.LaunchUrl.OpenPolicy) },
            )
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Code,
                text = LocalizationR.string.settings_item_source.asUiText(),
                onClick = { processIntent(SettingsIntent.LaunchUrl.OpenSourceCode) },
            )
            //endregion
        }

        AnimatedVisibility(visible = state.appVersion.isNotEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { processIntent(SettingsIntent.Action.AppVersion) },
                    ),
                text = stringResource(id = LocalizationR.string.version, state.appVersion),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
