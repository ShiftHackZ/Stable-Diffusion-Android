@file:OptIn(ExperimentalMaterial3Api::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
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
            state = viewModel.state.collectAsStateWithLifecycle().value,
            demoModeUrl = linksProvider.demoModeUrl,
            onNavigateBack = onNavigateBack,
            onServerModeUpdated = viewModel::updateServerMode,
            onServerUrlUpdated = viewModel::updateServerUrl,
            onAuthTypeSelected = viewModel::updateAuthType,
            onLoginUpdated = viewModel::updateLogin,
            onPasswordUpdated = viewModel::updatePassword,
            onTogglePasswordVisibility = viewModel::updatePasswordVisibility,
            onHordeApiKeyUpdated = viewModel::updateHordeApiKey,
            onDemoModeUpdated = viewModel::updateDemoMode,
            onHordeDefaultApiKeyUsageUpdated = viewModel::updateHordeDefaultApiKeyUsage,
            onServerInstructionsItemClick = { launchUrl(linksProvider.setupInstructionsUrl) },
            onOpenHordeWebSite = { launchUrl(linksProvider.hordeUrl) },
            onOpenHordeSignUpWebSite = { launchUrl(linksProvider.hordeSignUpUrl) },
            onDownloadCardButtonClick = viewModel::downloadClickReducer,
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
    onAuthTypeSelected: (ServerSetupState.AuthType) -> Unit = {},
    onLoginUpdated: (String) -> Unit = {},
    onPasswordUpdated: (String) -> Unit = {},
    onTogglePasswordVisibility: (Boolean) -> Unit = {},
    onHordeApiKeyUpdated: (String) -> Unit = {},
    onDemoModeUpdated: (Boolean) -> Unit = {},
    onHordeDefaultApiKeyUsageUpdated: (Boolean) -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onOpenHordeWebSite: () -> Unit = {},
    onOpenHordeSignUpWebSite: () -> Unit = {},
    onDownloadCardButtonClick: () -> Unit = {},
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
                    enabled = when (state.mode) {
                        ServerSetupState.Mode.LOCAL -> state.localModelDownloaded
                        else -> true
                    },
                ) {
                    Text(
                        text = stringResource(id = when (state.mode) {
                            ServerSetupState.Mode.LOCAL -> R.string.action_setup
                            else -> R.string.action_connect
                        })
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    if (state.allowedModes.size > 1) {
                        Column {
                            state.allowedModes.forEach { mode ->
                                ConfigurationModeButton(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 4.dp),
                                    state = state,
                                    mode = mode,
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
                            onAuthTypeSelected = onAuthTypeSelected,
                            onLoginUpdated = onLoginUpdated,
                            onPasswordUpdated = onPasswordUpdated,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            onDemoModeUpdated = onDemoModeUpdated,
                            onServerInstructionsItemClick = onServerInstructionsItemClick,
                        )
                        ServerSetupState.Mode.HORDE -> HordeAiSetupTab(
                            state = state,
                            onHordeApiKeyUpdated = onHordeApiKeyUpdated,
                            onHordeDefaultApiKeyUsageUpdated = onHordeDefaultApiKeyUsageUpdated,
                            onOpenHordeWebSite = onOpenHordeWebSite,
                            onOpenHordeSignUpWebSite = onOpenHordeSignUpWebSite,
                        )
                        ServerSetupState.Mode.LOCAL -> LocalDiffusionSetupTab(
                            state = state,
                            onDownloadCardButtonClick = onDownloadCardButtonClick,
                        )
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
    onAuthTypeSelected: (ServerSetupState.AuthType) -> Unit = {},
    onLoginUpdated: (String) -> Unit = {},
    onPasswordUpdated: (String) -> Unit = {},
    onTogglePasswordVisibility: (Boolean) -> Unit = {},
    onDemoModeUpdated: (Boolean) -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
) {
    Column(
        modifier = modifier
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
        val fieldModifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
        TextField(
            modifier = fieldModifier,
            value = if (!state.demoMode) state.serverUrl else demoModeUrl,
            onValueChange = onServerUrlUpdated,
            label = { Text(stringResource(id = R.string.hint_server_url)) },
            enabled = !state.demoMode,
            isError = state.serverUrlValidationError != null && !state.demoMode,
            supportingText = state.serverUrlValidationError
                ?.takeIf { !state.demoMode }
                ?.let { { Text(it.asString()) } },
        )
        if (!state.demoMode) {
            DropdownTextField(
                modifier = fieldModifier,
                label = "Authorization".asUiText(),
                items = ServerSetupState.AuthType.values().toList(),
                value = state.authType,
                onItemSelected = onAuthTypeSelected,
                displayDelegate = { type ->
                    when (type) {
                        ServerSetupState.AuthType.ANONYMOUS -> R.string.auth_anonymous
                        ServerSetupState.AuthType.HTTP_BASIC -> R.string.auth_http_basic
                    }.asUiText()
                }
            )
            when (state.authType) {
                ServerSetupState.AuthType.HTTP_BASIC -> {

                    TextField(
                        modifier = fieldModifier,
                        value = state.login,
                        onValueChange = onLoginUpdated,
                        label = { Text(stringResource(id = R.string.hint_login)) },
                        isError = state.loginValidationError != null,
                        supportingText = state.loginValidationError?.let {
                            { Text(it.asString()) }
                        },
                    )
                    TextField(
                        modifier = fieldModifier,
                        value = state.password,
                        onValueChange = onPasswordUpdated,
                        label = { Text(stringResource(id = R.string.hint_password)) },
                        isError = state.passwordValidationError != null,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (state.passwordVisible) VisualTransformation.None
                        else PasswordVisualTransformation(),
                        supportingText = state.passwordValidationError?.let {
                            { Text(it.asString()) }
                        },
                        trailingIcon = {
                            val image = if (state.passwordVisible) Icons.Filled.Visibility
                            else Icons.Filled.VisibilityOff
                            val description = if (state.passwordVisible) "Hide password" else "Show password"
                            IconButton(
                                onClick = { onTogglePasswordVisibility(state.passwordVisible) },
                                content = { Icon(image, description) },
                            )
                        }
                    )
                }
                else -> Unit
            }
        }
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
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
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
private fun HordeAiSetupTab(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    onHordeApiKeyUpdated: (String) -> Unit = {},
    onHordeDefaultApiKeyUsageUpdated: (Boolean) -> Unit = {},
    onOpenHordeWebSite: () -> Unit = {},
    onOpenHordeSignUpWebSite: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_server_horde_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.hint_server_horde_sub_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            value = if (state.hordeDefaultApiKey) Constants.HORDE_DEFAULT_API_KEY else state.hordeApiKey,
            onValueChange = onHordeApiKeyUpdated,
            label = { Text(stringResource(id = R.string.hint_server_horde_api_key)) },
            enabled = !state.hordeDefaultApiKey,
            isError = state.hordeApiKeyValidationError != null && !state.hordeDefaultApiKey,
            supportingText = {
                state.hordeApiKeyValidationError
                    ?.takeIf { !state.hordeDefaultApiKey }
                    ?.let { Text(it.asString()) }
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.hordeDefaultApiKey,
                onCheckedChange = onHordeDefaultApiKeyUsageUpdated,
            )
            Text(text = stringResource(id = R.string.hint_server_horde_use_default_api_key))
        }
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.Help,
            text = R.string.hint_server_horde_about.asUiText(),
            onClick = onOpenHordeWebSite,
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.Api,
            text = R.string.hint_server_horde_get_api_key.asUiText(),
            onClick = onOpenHordeSignUpWebSite,
        )
        Text(
            modifier = Modifier.padding(bottom = 16.dp, top = 8.dp),
            text = stringResource(id = R.string.hint_server_horde_usage),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
    }
}

@Composable
private fun LocalDiffusionSetupTab(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    onDownloadCardButtonClick: () -> Unit = {},
) {
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_local_diffusion_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.hint_local_diffusion_sub_title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.secondary,
        )
        Column(
            modifier = modifier
                .padding(top = 24.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.primaryContainer)
                .defaultMinSize(minHeight = 50.dp),
        ) {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
            ) {
                val icon = when (state.downloadState) {
                    is DownloadState.Downloading -> Icons.Outlined.FileDownload
                    else -> {
                        if (state.localModelDownloaded) Icons.Outlined.FileDownloadDone
                        else Icons.Outlined.FileDownloadOff
                    }
                }
                Icon(
                    modifier = modifier
                        .padding(horizontal = 8.dp)
                        .size(48.dp),
                    imageVector = icon,
                    contentDescription = "Download state",
                )
                Column(
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.model_local_diffusion),
                    )
                    Text(
                        text = stringResource(id = R.string.model_local_diffusion_size),
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Button(
                    modifier = Modifier.padding(end = 8.dp),
                    onClick = onDownloadCardButtonClick,
                ) {
                    Text(
                        text = stringResource(id = when (state.downloadState) {
                            is DownloadState.Downloading -> R.string.cancel
                            is DownloadState.Error -> R.string.retry
                            else -> {
                                if (state.localModelDownloaded) R.string.delete
                                else R.string.download
                            }
                        }),
                    )
                }
            }
            when (state.downloadState) {
                is DownloadState.Downloading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        progress = state.downloadState.percent / 100f,
                    )
                }
                is DownloadState.Error -> {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp),
                        text = stringResource(id = R.string.error_download_fail),
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                else -> Unit
            }
        }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.hint_local_diffusion_warning),
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
                ServerSetupState.Mode.HORDE -> Icons.Default.Cloud
                ServerSetupState.Mode.LOCAL -> Icons.Default.Android
            },
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSecondaryContainer,
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 8.dp, bottom = 8.dp),
            text = stringResource(id = when (mode) {
                ServerSetupState.Mode.SD_AI_CLOUD -> R.string.srv_type_cloud
                ServerSetupState.Mode.OWN_SERVER -> R.string.srv_type_own
                ServerSetupState.Mode.HORDE -> R.string.srv_type_horde
                ServerSetupState.Mode.LOCAL -> R.string.srv_type_local
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
