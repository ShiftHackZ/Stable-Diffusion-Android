@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.UiText
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.EmptyEffect
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.DecisionInteractiveDialog
import com.shifthackz.aisdv1.presentation.widget.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.ProgressDialog
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SettingsScreen(
    private val viewModel: SettingsViewModel,
    private val launchSetup: () -> Unit = {},
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
            onClearAppCacheItemClick = viewModel::launchClearAppCacheDialog,
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
    onClearAppCacheItemClick: () -> Unit = {},
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
                        onClearAppCacheItemClick = onClearAppCacheItemClick,
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
                    title = "Select sd model".asUiText(),
                    text = UiText.empty,
                    confirmActionResId = R.string.action_select,
                    onConfirmAction = { onSdModelSelected(selectedItem) },
                    onDismissRequest = onDismissScreenDialog,
                    content = {
                        DropdownTextField(
                            modifier = Modifier.fillMaxWidth(),
                            label = "Selected model".asUiText(),
                            value = selectedItem,
                            items = (state.screenDialog as SettingsState.Dialog.SelectSdModel).models,
                            onItemSelected = { selectedItem = it },
                        )
                    }
                )
            }
            SettingsState.Dialog.ClearAppCache -> DecisionInteractiveDialog(
                title = "Clear app cache".asUiText(),
                text = "This will reset app settings and delete all the generated images. Do you want to proceed?".asUiText(),
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
    onClearAppCacheItemClick: () -> Unit = {},
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
            startIcon = Icons.Default.DeleteForever,
            text = R.string.settings_item_clear_cache.asUiText(),
            onClick = onClearAppCacheItemClick,
        )

        Text(
            modifier = headerModifier,
            text = stringResource(id = R.string.settings_header_info),
            style = MaterialTheme.typography.headlineSmall,
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
private fun SettingsItem(
    modifier: Modifier = Modifier,
    startIcon: ImageVector,
    text: UiText,
    endValueText: UiText = UiText.empty,
    endValueContent: @Composable () -> Unit = {},
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = MaterialTheme.colorScheme.primaryContainer)
            .defaultMinSize(minHeight = 50.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                modifier = Modifier.padding(horizontal = 8.dp),
                imageVector = startIcon,
                contentDescription = null,
            )
            Text(
                text = text.asString(),
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            endValueContent()
            val value = endValueText.asString()
            if (value.isNotEmpty()) Text(
                modifier = Modifier.fillMaxWidth(0.5f),
                text = endValueText.asString(),
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Right,
            )
            Icon(
                modifier = Modifier.padding(horizontal = 6.dp),
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
            )
        }
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
            showRateGooglePlay = true,
        )
    )
}
