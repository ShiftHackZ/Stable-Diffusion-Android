@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    private val onServerSetupComplete: () -> Unit,
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
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                ) {

                    Text(
                        modifier = Modifier.padding(vertical = 32.dp),
                        text = stringResource(id = R.string.hint_server_setup_title),
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center,
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

                    Text(
                        modifier = Modifier.padding(vertical = 32.dp),
                        text = stringResource(
                            if (state.demoMode) R.string.hint_demo_mode
                            else R.string.hint_valid_urls,
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary,
                    )
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
@Preview(showSystemUi = true, showBackground = true)
private fun PreviewServerSetup() {
    ScreenContent(
        state = ServerSetupState(serverUrl = "https://moroz.cc"),
        demoModeUrl = "https://sdai.moroz.cc",
    )
}
