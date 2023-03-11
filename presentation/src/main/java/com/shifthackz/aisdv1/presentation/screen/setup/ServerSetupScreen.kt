@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.widget.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.ProgressDialog

class ServerSetupScreen(
    private val viewModel: ServerSetupViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val onServerSetupComplete: () -> Unit,
) : MviScreen<ServerSetupState, ServerSetupEffect>(viewModel) {

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsState().value,
            onNavigateBack = onNavigateBack,
            onServerUrlUpdated = viewModel::updateServerUrl,
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
    onNavigateBack: () -> Unit = {},
    onServerUrlUpdated: (String) -> Unit = {},
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
//                enabled = !state.hasValidationErrors && !state.imageState.isEmpty
                ) {
                    Text(
                        text = stringResource(id = R.string.action_connect)
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp),
                ) {

                    Text(
                        modifier = Modifier.padding(vertical = 32.dp),
                        text = stringResource(id = R.string.hint_server_setup_title),
                        style = MaterialTheme.typography.headlineSmall,
//                    color = MaterialTheme.colorScheme.primaryC,
                        textAlign = TextAlign.Center,
                    )

                    TextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        value = state.serverUrl,
                        onValueChange = onServerUrlUpdated,
                        label = { Text(stringResource(id = R.string.hint_server_url)) },
                        isError = state.validationError != null,
                        supportingText = { state.validationError?.let { Text(it.asString()) } },
                    )

                    Text(
                        modifier = Modifier.padding(vertical = 32.dp),
                        text = stringResource(id = R.string.hint_valid_urls),
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
    ScreenContent(state = ServerSetupState(serverUrl = "https://moroz.cc"))
}