@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerSetupScreen(
    private val viewModel: ServerSetupViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val onServerSetupComplete: () -> Unit = {},
    private val launchUrl: (String) -> Unit = {},
) : MviScreen<ServerSetupState, ServerSetupEffect>(viewModel), KoinComponent {

    private val linksProvider: LinksProvider by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            demoModeUrl = linksProvider.demoModeUrl,
            onNavigateBack = onNavigateBack,
            onServerModeUpdated = viewModel::updateServerMode,
            onServerUrlUpdated = viewModel::updateServerUrl,
            onDemoModeUpdated = viewModel::updateDemoMode,
            onServerInstructionsItemClick = { launchUrl(linksProvider.setupInstructionsUrl) },
            onSetupButtonClick = viewModel::connectToServer,
            onDismissScreenDialog = viewModel::dismissScreenDialog,
        )
    }

    override fun processEffect(effect: ServerSetupEffect) = when (effect) {
        ServerSetupEffect.CompleteSetup -> onServerSetupComplete()
    }
}

@Composable
private fun ScreenContent(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    demoModeUrl: String,
    onNavigateBack: () -> Unit = {},
    onServerModeUpdated: (ServerSetupState.Mode) -> Unit = {},
    onServerUrlUpdated: (String) -> Unit = {},
    onDemoModeUpdated: (Boolean) -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onSetupButtonClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.title_server_setup),
                            style = MaterialTheme.typography.headlineMedium,
                        )
                    },
                    navigationIcon = {
                        if (state.showBackNavArrow) IconButton(
                            onClick = onNavigateBack,
                            content = {
                                Icon(
                                    Icons.Outlined.ArrowBack,
                                    contentDescription = "Back button",
                                )
                            },
                        )
                    },
                )
            },
            bottomBar = {
                Button(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 32.dp)
                        .padding(bottom = 16.dp),
                    onClick = onSetupButtonClick,
                ) {
                    Text(
                        text = stringResource(id = R.string.action_connect)
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier.padding(paddingValues),
                ) {
                    if (state.allowedModes.size > 1) {
                        LazyColumn {
                            items(state.allowedModes.size) { index ->
                                ConfigurationModeButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    state = state,
                                    mode = state.allowedModes[index],
                                    onClick = onServerModeUpdated,
                                )
                            }
                        }
                    }
                    when (state.mode) {
                        ServerSetupState.Mode.SD_AI_CLOUD -> SdaiCloudSetupTab()
                        ServerSetupState.Mode.OWN_SERVER -> OwnServerSetupTab(
                            state = state,
                            demoModeUrl =  demoModeUrl,
                            onServerUrlUpdated = onServerUrlUpdated,
                            onDemoModeUpdated = onDemoModeUpdated,
                            onServerInstructionsItemClick = onServerInstructionsItemClick,
                        )
                        else -> {}
                    }
                }
            },
        )
        when (state.screenDialog) {
            ServerSetupState.Dialog.Communicating -> ProgressDialog(
                canDismiss = false,
            )
            is ServerSetupState.Dialog.Error -> ErrorDialog(
                text = state.screenDialog.error,
                onDismissScreenDialog,
            )
            ServerSetupState.Dialog.None -> Unit
        }
    }
}

@Composable
private fun OwnServerSetupTab(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    demoModeUrl: String,
    onServerUrlUpdated: (String) -> Unit = {},
    onDemoModeUpdated: (Boolean) -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_server_setup_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = if (!state.demoMode) state.serverUrl else demoModeUrl,
            onValueChange = onServerUrlUpdated,
            label = { Text(stringResource(id = R.string.hint_server_url)) },
            enabled = !state.demoMode,
            isError = state.validationError != null && !state.demoMode,
            supportingText = {
                state.validationError
                    ?.takeIf { !state.demoMode }
                    ?.let { Text(it.asString()) }
            },
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.Help,
            text = R.string.settings_item_instructions.asUiText(),
            onClick = onServerInstructionsItemClick,
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.DeveloperMode,
            text = R.string.settings_item_demo.asUiText(),
            onClick = { onDemoModeUpdated(!state.demoMode) },
            endValueContent = {
                Switch(
                    modifier = Modifier.padding(horizontal = 8.dp),
                    checked = state.demoMode,
                    onCheckedChange = onDemoModeUpdated,
                )
            }
        )
        if (!state.demoMode) Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(id = R.string.hint_args_warning),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(
            modifier = Modifier.padding(top = 8.dp),
            text = stringResource(
                if (state.demoMode) R.string.hint_demo_mode
                else R.string.hint_valid_urls,
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun SdaiCloudSetupTab(
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_server_sdai_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = stringResource(id = R.string.hint_server_sdai_sub_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun ConfigurationModeButton(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    mode: ServerSetupState.Mode,
    onClick: (ServerSetupState.Mode) -> Unit = {},
) {
    Row(
        modifier = modifier
            .background(
                MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (state.mode == mode) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.secondaryContainer,
            )
            .clickable { onClick(mode) },
    ) {
        Icon(
            modifier = Modifier
                .size(42.dp)
                .padding(top = 8.dp, bottom = 8.dp),
            imageVector = when (mode) {
                ServerSetupState.Mode.SD_AI_CLOUD -> Icons.Default.Cloud
                ServerSetupState.Mode.OWN_SERVER -> Icons.Default.Computer
                ServerSetupState.Mode.HORDE -> Icons.Default.Class
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically).padding(top = 8.dp, bottom = 8.dp),
            text = stringResource(id = when (mode) {
                ServerSetupState.Mode.SD_AI_CLOUD -> R.string.srv_type_cloud
                ServerSetupState.Mode.OWN_SERVER -> R.string.srv_type_own
                ServerSetupState.Mode.HORDE -> R.string.src_type_horde
            }),
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSecondaryContainer,
            textAlign = TextAlign.Center,
            lineHeight = 15.sp,
        )
    }
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewServerSetupSDAI() {
    ScreenContent(
        state = ServerSetupState(serverUrl = "https://moroz.cc"),
        demoModeUrl = "https://sdai.moroz.cc",
    )
}

@Composable
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewServerSetupOwn() {
    ScreenContent(
        state = ServerSetupState(mode = ServerSetupState.Mode.OWN_SERVER),
        demoModeUrl = "https://sdai.moroz.cc",
    )
}
