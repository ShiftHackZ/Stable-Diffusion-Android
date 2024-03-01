@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.AutoFixNormal
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.DynamicForm
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.SettingsEthernet
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsScreen(
    private val viewModel: SettingsViewModel,
    private val launchSetup: () -> Unit = {},
    private val launchUrl: (String) -> Unit = {},
    private val launchDebugMenu: () -> Unit = {},
    private val shareLogFile: () -> Unit = {},
    private val requestStoragePermissions: () -> Unit,
) : MviScreen<SettingsState, SettingsEffect>(viewModel), KoinComponent {

    private val linksProvider: LinksProvider by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            onConfigurationItemClick = launchSetup,
            onSdModelItemClick = viewModel::launchSdModelSelectionDialog,
            onLocalUseNNAPIChanged = viewModel::changeLocalUseNNAPISetting,
            onMonitorConnectivityChanged = viewModel::changeMonitorConnectivitySetting,
            onAutoSaveAiResultChanged = viewModel::changeAutoSaveAiResultSetting,
            onSaveToMediaStoreChanged = viewModel::changeSaveToMediaStoreSetting,
            onFormAdvancedOptionsAlwaysShowChanged = viewModel::changeFormAdvancedOptionsAlwaysShow,
            onClearAppCacheItemClick = viewModel::launchClearAppCacheDialog,
            onReportProblemItemClick = shareLogFile,
            onPolicyItemClick = { launchUrl(linksProvider.privacyPolicyUrl) },
            onServerInstructionsItemClick = { launchUrl(linksProvider.setupInstructionsUrl) },
            onGetSourceItemClick = { launchUrl(linksProvider.gitHubSourceUrl) },
            onAppVersionClick = launchDebugMenu,
            onSdModelSelected = viewModel::selectStableDiffusionModel,
            onClearAppCacheConfirm = viewModel::clearAppCache,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit

    override fun processEffect(effect: SettingsEffect) = when (effect) {
        SettingsEffect.RequestStoragePermission -> requestStoragePermissions()
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onConfigurationItemClick: () -> Unit = {},
    onSdModelItemClick: () -> Unit = {},
    onLocalUseNNAPIChanged: (Boolean) -> Unit = {},
    onMonitorConnectivityChanged: (Boolean) -> Unit = {},
    onAutoSaveAiResultChanged: (Boolean) -> Unit = {},
    onSaveToMediaStoreChanged: (Boolean) -> Unit = {},
    onFormAdvancedOptionsAlwaysShowChanged: (Boolean) -> Unit = {},

    onClearAppCacheItemClick: () -> Unit = {},
    onReportProblemItemClick: () -> Unit = {},
    onPolicyItemClick: () -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onGetSourceItemClick: () -> Unit = {},
    onAppVersionClick: () -> Unit = {},

    onSdModelSelected: (String) -> Unit = {},
    onClearAppCacheConfirm: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
    onDismissBottomSheet: () -> Unit = {},
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
                val contentModifier = Modifier.padding(paddingValues)
                when (state) {
                    SettingsState.Uninitialized -> Text("Load")
                    is SettingsState.Content -> ContentSettingsState(
                        modifier = contentModifier.padding(horizontal = 16.dp),
                        state = state,
                        onConfigurationItemClick = onConfigurationItemClick,
                        onSdModelItemClick = onSdModelItemClick,
                        onLocalUseNNAPIChanged = onLocalUseNNAPIChanged,
                        onMonitorConnectivityChanged = onMonitorConnectivityChanged,
                        onAutoSaveAiResultChanged = onAutoSaveAiResultChanged,
                        onSaveToMediaStoreChanged = onSaveToMediaStoreChanged,
                        onFormAdvancedOptionsAlwaysShowChanged = onFormAdvancedOptionsAlwaysShowChanged,
                        onClearAppCacheItemClick = onClearAppCacheItemClick,
                        onReportProblemItemClick = onReportProblemItemClick,
                        onPolicyItemClick = onPolicyItemClick,
                        onServerInstructionsItemClick = onServerInstructionsItemClick,
                        onGetSourceItemClick = onGetSourceItemClick,
                        onAppVersionClick = onAppVersionClick,
                    )
                }
            }
        )
        when (state.screenDialog) {
            SettingsState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            SettingsState.Dialog.None -> Unit
            is SettingsState.Dialog.SelectSdModel -> {
                var selectedItem by remember {
                    mutableStateOf(
                        (state.screenDialog as SettingsState.Dialog.SelectSdModel).selected,
                    )
                }
                DecisionInteractiveDialog(
                    title = R.string.title_select_sd_model.asUiText(),
                    text = UiText.empty,
                    confirmActionResId = R.string.action_select,
                    onConfirmAction = { onSdModelSelected(selectedItem) },
                    onDismissRequest = onDismissScreenDialog,
                    content = {
                        DropdownTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = R.string.hint_sd_model.asUiText(),
                            value = selectedItem,
                            items = (state.screenDialog as SettingsState.Dialog.SelectSdModel).models,
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
                onDismissRequest = onDismissScreenDialog,
                onConfirmAction = onClearAppCacheConfirm,
            )
        }
    }
}

@Composable
private fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState.Content,
    onConfigurationItemClick: () -> Unit = {},
    onSdModelItemClick: () -> Unit = {},
    onLocalUseNNAPIChanged: (Boolean) -> Unit = {},
    onAutoSaveAiResultChanged: (Boolean) -> Unit = {},
    onSaveToMediaStoreChanged: (Boolean) -> Unit = {},
    onFormAdvancedOptionsAlwaysShowChanged: (Boolean) -> Unit = {},
    onMonitorConnectivityChanged: (Boolean) -> Unit = {},
    onClearAppCacheItemClick: () -> Unit = {},
    onReportProblemItemClick: () -> Unit = {},
    onPolicyItemClick: () -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onGetSourceItemClick: () -> Unit = {},
    onAppVersionClick: () -> Unit = {},
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
            onClick = onConfigurationItemClick,
        )
        if (state.showSdModelSelector) SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.AutoFixNormal,
            text = R.string.settings_item_sd_model.asUiText(),
            endValueText = state.sdModelSelected.asUiText(),
            onClick = onSdModelItemClick,
        )
        if (state.showLocalUseNNAPI) {
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.AccountTree,
                text = R.string.settings_item_local_nnapi.asUiText(),
                endValueText = state.sdModelSelected.asUiText(),
                onClick = { onLocalUseNNAPIChanged(!state.localUseNNAPI) },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.localUseNNAPI,
                        onCheckedChange = onLocalUseNNAPIChanged,
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
            onClick = { onMonitorConnectivityChanged(!state.monitorConnectivity) },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.monitorConnectivity,
                    onCheckedChange = onMonitorConnectivityChanged,
                )
            }
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Save,
            text = R.string.settings_item_auto_save.asUiText(),
            onClick = { onAutoSaveAiResultChanged(!state.autoSaveAiResults) },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.autoSaveAiResults,
                    onCheckedChange = onAutoSaveAiResultChanged,
                )
            }
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Folder,
            text = R.string.settings_item_auto_save_media_store.asUiText(),
            onClick = { onSaveToMediaStoreChanged(!state.saveToMediaStore) },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.saveToMediaStore,
                    onCheckedChange = onSaveToMediaStoreChanged,
                )
            }
        )
        if (state.showFormAdvancedOption) {
            SettingsItem(
                modifier = itemModifier,
                startIcon = Icons.Default.DynamicForm,
                text = R.string.settings_item_advanced_form_default.asUiText(),
                onClick = {
                    onFormAdvancedOptionsAlwaysShowChanged(!state.formAdvancedOptionsAlwaysShow)
                },
                endValueContent = {
                    Switch(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        checked = state.formAdvancedOptionsAlwaysShow,
                        onCheckedChange = onFormAdvancedOptionsAlwaysShowChanged,
                    )
                }
            )
        }
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.DeleteForever,
            text = R.string.settings_item_clear_cache.asUiText(),
            onClick = onClearAppCacheItemClick,
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
            onClick = onReportProblemItemClick,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Gavel,
            text = R.string.settings_item_policy.asUiText(),
            onClick = onPolicyItemClick,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Help,
            text = R.string.settings_item_instructions.asUiText(),
            onClick = onServerInstructionsItemClick,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Code,
            text = R.string.settings_item_source.asUiText(),
            onClick = onGetSourceItemClick,
        )

        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onAppVersionClick,
                ),
            text = stringResource(id = R.string.version, state.appVersion),
            style = MaterialTheme.typography.labelMedium,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewStateContent() {
    ScreenContent(
        modifier = Modifier.fillMaxSize(),
        state = SettingsState.Content(
            sdModels = listOf("Stable diffusion v1.5"),
            sdModelSelected = "Stable diffusion v1.5",
            appVersion = "1.0.0 (10)",
            localUseNNAPI = false,
            monitorConnectivity = true,
            autoSaveAiResults = true,
            saveToMediaStore = true,
            formAdvancedOptionsAlwaysShow = false,
            showSdModelSelector = true,
            showMonitorConnectionOption = true,
            showLocalUseNNAPI = true,
            showFormAdvancedOption = true,
        )
    )
}
