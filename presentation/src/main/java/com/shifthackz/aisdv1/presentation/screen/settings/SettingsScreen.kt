@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.modal.ModalRenderer
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.aisdv1.presentation.utils.ReportProblemEmailComposer
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
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
                if (!state.loading) {
                    val contentModifier = Modifier.padding(paddingValues)
                    ContentSettingsState(
                        modifier = contentModifier.padding(horizontal = 16.dp),
                        state = state,
                        processIntent = processIntent,
                    )
                }
            }
        )
        ModalRenderer(screenModal = state.screenModal) {
            (it as? SettingsIntent)?.let(processIntent::invoke)
        }
        /*
        when (state.screenDialog) {
            SettingsState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )

            SettingsState.Dialog.None -> Unit
            is SettingsState.Dialog.SelectSdModel -> {
                var selectedItem by remember {
                    mutableStateOf(state.screenDialog.selected,)
                }
                DecisionInteractiveDialog(
                    title = R.string.title_select_sd_model.asUiText(),
                    text = UiText.empty,
                    confirmActionResId = R.string.action_select,
                    onConfirmAction = { processIntent(SettingsIntent.SdModel.Select(selectedItem)) },
                    onDismissRequest = { processIntent(SettingsIntent.DismissDialog) },
                    content = {
                        DropdownTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = R.string.hint_sd_model.asUiText(),
                            value = selectedItem,
                            items = state.screenDialog.models,
                            onItemSelected = { selectedItem = it },
                        )
                    }
                )
            }

            SettingsState.Dialog.ClearAppCache -> DecisionInteractiveDialog(
                title = R.string.title_clear_app_cache.asUiText(),
                text = R.string.interaction_cache_sub_title.asUiText(),
                confirmActionResId = R.string.yes,
                dismissActionResId = R.string.no,
                onDismissRequest = { processIntent(SettingsIntent.DismissDialog) },
                onConfirmAction = { processIntent(SettingsIntent.Action.ClearAppCache.Confirm) },
            )
        }
         */
    }
}

@Composable
private fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState,
    processIntent: (SettingsIntent) -> Unit = {},
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
    ) {
        val headerModifier = Modifier.padding(vertical = 16.dp)
        val itemModifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp)

        Text(
            modifier = headerModifier,
            text = stringResource(id = R.string.settings_header_server),
            style = MaterialTheme.typography.headlineSmall,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.SettingsEthernet,
            text = R.string.settings_item_config.asUiText(),
            onClick = { processIntent(SettingsIntent.NavigateConfiguration) },
        )
        if (state.showSdModelSelector) SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.AutoFixNormal,
            text = R.string.settings_item_sd_model.asUiText(),
            endValueText = state.sdModelSelected.asUiText(),
            onClick = { processIntent(SettingsIntent.SdModel.OpenChooser) },
        )
        if (state.showLocalUseNNAPI) {
            SettingsItem(
                modifier = itemModifier,
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

        Text(
            modifier = headerModifier,
            text = stringResource(id = R.string.settings_header_app),
            style = MaterialTheme.typography.headlineSmall,
        )
        if (state.showMonitorConnectionOption) SettingsItem(
            modifier = itemModifier,
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
            startIcon = Icons.Default.Tag,
            text = "Tagged input".asUiText(),
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
            startIcon = Icons.Default.DeleteForever,
            text = R.string.settings_item_clear_cache.asUiText(),
            onClick = { processIntent(SettingsIntent.Action.ClearAppCache.Request)},
        )

        Text(
            modifier = headerModifier,
            text = stringResource(id = R.string.settings_header_info),
            style = MaterialTheme.typography.headlineSmall,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Report,
            text = R.string.settings_item_report_problem.asUiText(),
            onClick = { processIntent(SettingsIntent.Action.ReportProblem) },
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Gavel,
            text = R.string.settings_item_policy.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenPolicy) },
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.AutoMirrored.Filled.Help,
            text = R.string.settings_item_instructions.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenServerInstructions) },
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Code,
            text = R.string.settings_item_source.asUiText(),
            onClick = { processIntent(SettingsIntent.LaunchUrl.OpenSourceCode) },
        )

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
