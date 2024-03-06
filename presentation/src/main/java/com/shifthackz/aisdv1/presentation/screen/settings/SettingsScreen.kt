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
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.InvertColors
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.theme.colorTokenPalette
import com.shifthackz.aisdv1.presentation.theme.isSdAppInDarkTheme
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.aisdv1.presentation.utils.ReportProblemEmailComposer
import com.shifthackz.aisdv1.presentation.widget.color.AccentColorSelector
import com.shifthackz.aisdv1.presentation.widget.item.SettingsHeader
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItemContent
import com.shifthackz.android.compose.daynightswitch.DayNightSwitch
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen() {
    val viewModel = koinViewModel<SettingsViewModel>()
    val context = LocalContext.current
    val storagePermission = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (!result.values.any { !it }) {
            viewModel.processIntent(SettingsIntent.StoragePermissionGranted)
        }
    }
    MviComponent(
        viewModel = viewModel,
        processEffect = { effect ->
            when (effect) {
                SettingsEffect.RequestStoragePermission -> {
                    if (PermissionUtil.checkStoragePermission(context, storagePermission::launch)) {
                        viewModel.processIntent(SettingsIntent.StoragePermissionGranted)
                    }
                }
                SettingsEffect.ShareLogFile -> ReportProblemEmailComposer().invoke(context)
                is SettingsEffect.OpenUrl -> context.openUrl(effect.url)
            }
        },
        applySystemUiColors = false,
    ) { state, intentHandler ->
        ScreenContent(
            state = state,
            processIntent = intentHandler,
        )
    }
}


@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    processIntent: (SettingsIntent) -> Unit,
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_settings),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                )
            },
            content = { paddingValues ->
                ContentSettingsState(
                    modifier = Modifier
                        .padding(paddingValues)
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
    val systemUiController = rememberSystemUiController()
    val navBarColor = MaterialTheme.colorScheme.surface
    LaunchedEffect(
        state.useSystemDarkTheme,
        state.darkTheme,
    ) {
        systemUiController.setNavigationBarColor(navBarColor)
    }
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        val headerModifier = Modifier.padding(vertical = 16.dp)
        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)

        //region MAIN SETTINGS
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = R.string.settings_header_server.asUiText(),
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.SettingsEthernet,
            text = R.string.settings_item_config.asUiText(),
            endValueText = when (state.serverSource) {
                ServerSource.AUTOMATIC1111 -> R.string.srv_type_own_short
                ServerSource.HORDE -> R.string.srv_type_horde_short
                ServerSource.HUGGING_FACE -> R.string.srv_type_hugging_face_short
                ServerSource.OPEN_AI -> R.string.srv_type_open_ai
                ServerSource.LOCAL -> R.string.srv_type_local_short
            }.asUiText(),
            onClick = { processIntent(SettingsIntent.NavigateConfiguration) },
        )
        if (state.showSdModelSelector) SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.AutoFixNormal,
            text = R.string.settings_item_sd_model.asUiText(),
            endValueText = state.sdModelSelected.asUiText(),
            onClick = { processIntent(SettingsIntent.SdModel.OpenChooser) },
        )
        if (state.showLocalUseNNAPI) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.AccountTree,
                text = R.string.settings_item_local_nnapi.asUiText(),
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
            Text(
                text = stringResource(id = R.string.settings_item_local_nnapi_warning),
                style = MaterialTheme.typography.labelMedium,
            )
        }
        //endregion

        //region APP SETTINGS
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = R.string.settings_header_app.asUiText(),
        )
        if (state.showMonitorConnectionOption) SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Refresh,
            text = R.string.settings_item_monitor_connection.asUiText(),
            onClick = {
                processIntent(SettingsIntent.UpdateFlag.MonitorConnection(!state.monitorConnectivity))
            },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.monitorConnectivity,
                    onCheckedChange = { processIntent(SettingsIntent.UpdateFlag.MonitorConnection(it)) },
                )
            }
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Save,
            text = R.string.settings_item_auto_save.asUiText(),
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
            }
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Folder,
            text = R.string.settings_item_auto_save_media_store.asUiText(),
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
            }
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Tag,
            text = R.string.settings_item_tagged_input.asUiText(),
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
                text = R.string.settings_item_advanced_form_default.asUiText(),
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
                }
            )
        }
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.DeleteForever,
            text = R.string.settings_item_clear_cache.asUiText(),
            onClick = { processIntent(SettingsIntent.Action.ClearAppCache.Request)},
        )
        //endregion

        //region LOOK AND FEEL
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = R.string.settings_header_look_and_feel.asUiText(),
        )
        if (state.showUseSystemColorPalette) {
            SettingsItem(
                modifier = itemModifier,
                loading = state.loading,
                startIcon = Icons.Default.Save,
                text = R.string.settings_item_lf_dynamic_colors.asUiText(),
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
            text = R.string.settings_item_lf_system_dark_theme.asUiText(),
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
                text = R.string.settings_item_lf_dark_theme.asUiText(),
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
        AnimatedVisibility(visible = !state.loading) {
            val isDark = isSdAppInDarkTheme()
            val palette = colorTokenPalette(isDark = isDark)
            Column(
                modifier = itemModifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                    .defaultMinSize(minHeight = 50.dp),
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                SettingsItemContent(
                    text = R.string.settings_item_lf_accent_color.asUiText(),
                    icon = Icons.Default.ColorLens,
                )
                Spacer(modifier = Modifier.height(16.dp))
                AccentColorSelector(
                    modifier = Modifier.padding(horizontal = 10.dp),
                    palette = palette,
                    selectedToken = state.colorToken,
                    onSelected = { _, token ->
                        processIntent(SettingsIntent.NewColorToken(token))
                    }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
        }
        //endregion

        //region LINKS & OTHERS
        SettingsHeader(
            modifier = headerModifier,
            loading = state.loading,
            text = R.string.settings_header_info.asUiText(),
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Report,
            text = R.string.settings_item_report_problem.asUiText(),
            onClick = { processIntent(SettingsIntent.Action.ReportProblem) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Gavel,
            text = R.string.settings_item_policy.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenPolicy) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = R.string.settings_item_instructions.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenServerInstructions) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.MonetizationOn,
            text = R.string.settings_item_donate.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.Donate) },
        )
        SettingsItem(
            modifier = itemModifier,
            loading = state.loading,
            startIcon = Icons.Default.Code,
            text = R.string.settings_item_source.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenSourceCode) },
        )
        //endregion

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
                text = stringResource(id = R.string.version, state.appVersion),
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
            )
        }
    }
}
