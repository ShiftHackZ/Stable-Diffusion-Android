@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.dialog.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsScreen(
    private val viewModel: SettingsViewModel,
    private val launchSetup: () -> Unit = {},
    private val onCheckUpdatesItemClick: () -> Unit = {},
    private val launchInAppReview: () -> Unit = {},
    private val launchUrl: (String) -> Unit = {},
) : MviScreen<SettingsState, EmptyEffect>(viewModel), KoinComponent {

    private val linksProvider: LinksProvider by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onConfigurationItemClick = launchSetup,
            onSdModelItemClick = viewModel::launchSdModelSelectionDialog,
            onMonitorConnectivityChanged = viewModel::changeMonitorConnectivitySetting,
            onAutoSaveAiResultChanged = viewModel::changeAutoSaveAiResultSetting,
            onClearAppCacheItemClick = viewModel::launchClearAppCacheDialog,
            onCheckUpdatesItemClick = onCheckUpdatesItemClick,
            onRateUsItemClick = launchInAppReview,
            onServerInstructionsItemClick = { launchUrl(linksProvider.setupInstructionsUrl) },
            onGetSourceItemClick = { launchUrl(linksProvider.gitHubSourceUrl) },
            onSdModelSelected = viewModel::selectStableDiffusionModel,
            onClearAppCacheConfirm = viewModel::clearAppCache,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
            onDismissBottomSheet = viewModel::dismissBottomSheet,
        )
    }

    @Composable
    override fun ApplySystemUiColors() = Unit
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: SettingsState,
    onConfigurationItemClick: () -> Unit = {},
    onSdModelItemClick: () -> Unit = {},
    onMonitorConnectivityChanged: (Boolean) -> Unit = {},
    onAutoSaveAiResultChanged: (Boolean) -> Unit = {},

    onClearAppCacheItemClick: () -> Unit = {},
    onCheckUpdatesItemClick: () -> Unit = {},
    onRateUsItemClick: () -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onGetSourceItemClick: () -> Unit = {},

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
                        onMonitorConnectivityChanged = onMonitorConnectivityChanged,
                        onAutoSaveAiResultChanged = onAutoSaveAiResultChanged,
                        onClearAppCacheItemClick = onClearAppCacheItemClick,
                        onCheckUpdatesItemClick = onCheckUpdatesItemClick,
                        onRateUsItemClick = onRateUsItemClick,
                        onServerInstructionsItemClick = onServerInstructionsItemClick,
                        onGetSourceItemClick = onGetSourceItemClick,
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
        when (state.bottomSheet) {
            SettingsState.Sheet.None -> Unit
            SettingsState.Sheet.SelectLanguage -> ModalBottomSheet(
                onDismissRequest = onDismissBottomSheet,
            ) {

            }
        }
    }
}

@Composable
private fun ContentSettingsState(
    modifier: Modifier = Modifier,
    state: SettingsState.Content,
    onConfigurationItemClick: () -> Unit = {},
    onSdModelItemClick: () -> Unit = {},
    onAutoSaveAiResultChanged: (Boolean) -> Unit = {},
    onMonitorConnectivityChanged: (Boolean) -> Unit = {},
    onClearAppCacheItemClick: () -> Unit = {},
    onCheckUpdatesItemClick: () -> Unit = {},
    onRateUsItemClick: () -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onGetSourceItemClick: () -> Unit = {},
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
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.AutoFixNormal,
            text = R.string.settings_item_sd_model.asUiText(),
            endValueText = state.sdModelSelected.asUiText(),
            onClick = onSdModelItemClick,
        )

        Text(
            modifier = headerModifier,
            text = stringResource(id = R.string.settings_header_app),
            style = MaterialTheme.typography.headlineSmall,
        )
        SettingsItem(
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
            startIcon = Icons.Filled.GetApp,
            text = R.string.settings_item_check_updates.asUiText(),
            onClick = onCheckUpdatesItemClick,
        )
        if (state.showRateGooglePlay) SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Filled.Star,
            text = R.string.settings_item_rate.asUiText(),
            onClick = onRateUsItemClick,
        )
        SettingsItem(
            modifier = itemModifier,
            startIcon = Icons.Default.Gavel,
            text = R.string.settings_item_policy.asUiText(),
            onClick = {},
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
                .padding(vertical = 16.dp),
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
            monitorConnectivity = true,
            autoSaveAiResults = true,
            showRateGooglePlay = true,
        )
    )
}
