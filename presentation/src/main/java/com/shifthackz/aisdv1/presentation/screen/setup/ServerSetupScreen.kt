@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.links.LinksProvider
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.core.ui.MviScreen
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.utils.Constants
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialog
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ServerSetupScreen(
    private val viewModel: ServerSetupViewModel,
    private val onNavigateBack: () -> Unit = {},
    private val onServerSetupComplete: () -> Unit = {},
    private val launchUrl: (String) -> Unit = {},
    private val launchManageStoragePermission: () -> Unit = {},
) : MviScreen<ServerSetupState, ServerSetupEffect>(viewModel), KoinComponent {

    private val linksProvider: LinksProvider by inject()
    private val buildInfoProvider: BuildInfoProvider by inject()

    @Composable
    override fun Content() {
        ScreenContent(
            modifier = Modifier.fillMaxSize(),
            state = viewModel.state.collectAsStateWithLifecycle().value,
            buildInfoProvider = buildInfoProvider,
            demoModeUrl = linksProvider.demoModeUrl,
            onNavigateBack = onNavigateBack,
            launchManageStoragePermission = launchManageStoragePermission,
            onServerModeUpdated = viewModel::updateServerMode,
            onServerUrlUpdated = viewModel::updateServerUrl,
            onAuthTypeSelected = viewModel::updateAuthType,
            onLoginUpdated = viewModel::updateLogin,
            onPasswordUpdated = viewModel::updatePassword,
            onTogglePasswordVisibility = viewModel::updatePasswordVisibility,
            onHordeApiKeyUpdated = viewModel::updateHordeApiKey,
            onHuggingFaceApiKeyUpdated = viewModel::updateHuggingFaceApiKey,
            onHuggingFaceModelSelected = viewModel::updateHuggingFaceModel,
            onDemoModeUpdated = viewModel::updateDemoMode,
            onHordeDefaultApiKeyUsageUpdated = viewModel::updateHordeDefaultApiKeyUsage,
            onServerInstructionsItemClick = { launchUrl(linksProvider.setupInstructionsUrl) },
            onOpenHordeWebSite = { launchUrl(linksProvider.hordeUrl) },
            onOpenHordeSignUpWebSite = { launchUrl(linksProvider.hordeSignUpUrl) },
            onOpenHuggingFaceWebSite = { launchUrl(linksProvider.huggingFaceUrl) },
            onDownloadCardButtonClick = viewModel::localModelDownloadClickReducer,
            onSelectLocalModel = viewModel::localModelSelect,
            onAllowLocalCustomModel = viewModel::updateAllowLocalCustomModel,
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
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    demoModeUrl: String,
    onNavigateBack: () -> Unit = {},
    launchManageStoragePermission: () -> Unit = {},
    onServerModeUpdated: (ServerSource) -> Unit = {},
    onServerUrlUpdated: (String) -> Unit = {},
    onAuthTypeSelected: (ServerSetupState.AuthType) -> Unit = {},
    onLoginUpdated: (String) -> Unit = {},
    onPasswordUpdated: (String) -> Unit = {},
    onTogglePasswordVisibility: (Boolean) -> Unit = {},
    onHordeApiKeyUpdated: (String) -> Unit = {},
    onHuggingFaceApiKeyUpdated: (String) -> Unit = {},
    onHuggingFaceModelSelected: (String) -> Unit = {},
    onDemoModeUpdated: (Boolean) -> Unit = {},
    onHordeDefaultApiKeyUsageUpdated: (Boolean) -> Unit = {},
    onServerInstructionsItemClick: () -> Unit = {},
    onOpenHordeWebSite: () -> Unit = {},
    onOpenHordeSignUpWebSite: () -> Unit = {},
    onOpenHuggingFaceWebSite: () -> Unit = {},
    onDownloadCardButtonClick: (ServerSetupState.LocalModel) -> Unit = {},
    onSelectLocalModel: (ServerSetupState.LocalModel) -> Unit = {},
    onAllowLocalCustomModel: (Boolean) -> Unit = {},
    onSetupButtonClick: () -> Unit = {},
    onDismissScreenDialog: () -> Unit = {},
) {
    Box(modifier) {
        Scaffold(
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            topBar = {
                Column {
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
                }
            },
            bottomBar = {
                Button(
                    modifier = Modifier
                        .height(height = 60.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp),
                    onClick = onSetupButtonClick,
                    enabled = when (state.mode) {
                        ServerSource.LOCAL -> state.localModels.any {
                            it.downloaded && it.selected
                        }
                        else -> true
                    },
                ) {
                    Text(
                        text = stringResource(id = when (state.mode) {
                            ServerSource.LOCAL -> R.string.action_setup
                            else -> R.string.action_connect
                        }),
                        color = LocalContentColor.current,
                    )
                }
            },
            content = { paddingValues ->
                Column(
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(paddingValues),
                ) {
                    Spacer(modifier = Modifier.height(12.dp))
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
                        ServerSource.AUTOMATIC1111 -> OwnServerSetupTab(
                            state = state,
                            demoModeUrl = demoModeUrl,
                            onServerUrlUpdated = onServerUrlUpdated,
                            onAuthTypeSelected = onAuthTypeSelected,
                            onLoginUpdated = onLoginUpdated,
                            onPasswordUpdated = onPasswordUpdated,
                            onTogglePasswordVisibility = onTogglePasswordVisibility,
                            onDemoModeUpdated = onDemoModeUpdated,
                            onServerInstructionsItemClick = onServerInstructionsItemClick,
                        )

                        ServerSource.HORDE -> HordeAiSetupTab(
                            state = state,
                            onHordeApiKeyUpdated = onHordeApiKeyUpdated,
                            onHordeDefaultApiKeyUsageUpdated = onHordeDefaultApiKeyUsageUpdated,
                            onOpenHordeWebSite = onOpenHordeWebSite,
                            onOpenHordeSignUpWebSite = onOpenHordeSignUpWebSite,
                        )

                        ServerSource.LOCAL -> LocalDiffusionSetupTab(
                            state = state,
                            buildInfoProvider = buildInfoProvider,
                            launchManageStoragePermission = launchManageStoragePermission,
                            onDownloadCardButtonClick = onDownloadCardButtonClick,
                            onSelectLocalModel = onSelectLocalModel,
                            onAllowLocalCustomModel = onAllowLocalCustomModel,
                        )

                        ServerSource.HUGGING_FACE -> HuggingFaceSetupTab(
                            state = state,
                            onHuggingFaceApiKeyUpdated = onHuggingFaceApiKeyUpdated,
                            onHuggingFaceModelSelected = onHuggingFaceModelSelected,
                            onOpenHuggingFaceWebSite = onOpenHuggingFaceWebSite,
                        )
                    }
                    Spacer(modifier = Modifier.height(32.dp))
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
                ?.let { { Text(it.asString(), color = MaterialTheme.colorScheme.error) } },
        )
        if (!state.demoMode) {
            DropdownTextField(
                modifier = fieldModifier,
                label = "Authorization".asUiText(),
                items = ServerSetupState.AuthType.entries,
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
                            { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
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
                            { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
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
        )
        Text(
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp),
            text = stringResource(
                if (state.demoMode) R.string.hint_demo_mode
                else R.string.hint_valid_urls,
            ),
            style = MaterialTheme.typography.bodyMedium,
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
                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
            },
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = state.hordeDefaultApiKey,
                onCheckedChange = onHordeDefaultApiKeyUsageUpdated,
            )
            Text(
                modifier = Modifier.padding(start = 8.dp),
                text = stringResource(id = R.string.hint_server_horde_use_default_api_key),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
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
        )
    }
}

@Composable
private fun HuggingFaceSetupTab(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    onHuggingFaceApiKeyUpdated: (String) -> Unit = {},
    onHuggingFaceModelSelected: (String) -> Unit = {},
    onOpenHuggingFaceWebSite: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val bringIntoViewRequester = remember { BringIntoViewRequester() }
    Column(
        modifier = modifier.padding(horizontal = 16.dp),
    ) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 8.dp),
            text = stringResource(id = R.string.hint_hugging_face_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.hint_hugging_face_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        TextField(
            modifier = Modifier
                .bringIntoViewRequester(bringIntoViewRequester)
                .fillMaxWidth()
                .padding(top = 8.dp)
                .onFocusChanged {
                    if (it.isFocused) {
                        coroutineScope.launch {
                            delay(400L)
                            bringIntoViewRequester.bringIntoView()
                        }
                    }
                },
            value = state.huggingFaceApiKey,
            onValueChange = {
                onHuggingFaceApiKeyUpdated(it)
                coroutineScope.launch {
                    bringIntoViewRequester.bringIntoView()
                }
            },
            label = { Text(stringResource(id = R.string.hint_server_horde_api_key)) },
            isError = state.huggingFaceApiKeyValidationError != null,
            supportingText = {
                state.huggingFaceApiKeyValidationError
                    ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
            },
        )
        DropdownTextField(
//            modifier = Modifier.padding(top = 4.dp),
            label = R.string.hint_hugging_face_model.asUiText(),
            items = state.huggingFaceModels,
            value = state.huggingFaceModel,
            onItemSelected = onHuggingFaceModelSelected,
        )
        SettingsItem(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            startIcon = Icons.Default.Help,
            text = R.string.hint_hugging_face_about.asUiText(),
            onClick = onOpenHuggingFaceWebSite,
        )
    }
}

@Composable
private fun LocalDiffusionSetupTab(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    launchManageStoragePermission: () -> Unit = {},
    onDownloadCardButtonClick: (ServerSetupState.LocalModel) -> Unit = {},
    onSelectLocalModel: (ServerSetupState.LocalModel) -> Unit = {},
    onAllowLocalCustomModel: (Boolean) -> Unit = {},
) {
    val modelItemUi: @Composable (ServerSetupState.LocalModel) -> Unit = { model ->
        Column(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f))
                .defaultMinSize(minHeight = 50.dp)
                .border(
                    width = 2.dp,
                    shape = RoundedCornerShape(16.dp),
                    color = if (model.selected) MaterialTheme.colorScheme.primary else Color.Transparent,
                )
                .clickable { onSelectLocalModel(model) },
        ) {
            Row(
                modifier = Modifier.padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                val icon = when (model.downloadState) {
                    is DownloadState.Downloading -> Icons.Outlined.FileDownload
                    else -> when {
                        model.id == LocalAiModel.CUSTOM.id -> Icons.Outlined.Landslide
                        model.downloaded -> Icons.Outlined.FileDownloadDone
                        else -> Icons.Outlined.FileDownloadOff
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
                    Text(text = model.name)
                    if (model.id != LocalAiModel.CUSTOM.id) {
                        Text(model.size)
                    }
                }
                Spacer(modifier = Modifier.weight(1f))
                if (model.id != LocalAiModel.CUSTOM.id) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { onDownloadCardButtonClick(model) },
                    ) {
                        Text(
                            text = stringResource(
                                id = when (model.downloadState) {
                                    is DownloadState.Downloading -> R.string.cancel
                                    is DownloadState.Error -> R.string.retry
                                    else -> {
                                        if (model.downloaded) R.string.delete
                                        else R.string.download
                                    }
                                }
                            ),
                            color = LocalContentColor.current,
                        )
                    }
                }
            }
            if (model.id == LocalAiModel.CUSTOM.id) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = stringResource(id = R.string.model_local_custom_title),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = R.string.model_local_custom_sub_title),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    fun folderModifier(treeNum: Int) =
                        Modifier.padding(start = (treeNum - 1) * 12.dp)

                    val folderStyle = MaterialTheme.typography.bodySmall
                    Text(
                        modifier = folderModifier(1),
                        text = "Download",
                        style = folderStyle,
                    )
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = "SDAI",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(3),
                        text = "model",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(4),
                        text = "text_encoder",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "model.ort",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(4),
                        text = "tokenizer",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "merges.txt",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "special_tokens_map.json",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "tokenizer_config.json",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "vocab.json",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(4),
                        text = "unet",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "model.ort",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(4),
                        text = "vae_decoder",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(5),
                        text = "model.ort",
                        style = folderStyle,
                    )
                }
            }
            when (model.downloadState) {
                is DownloadState.Downloading -> {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                        progress = model.downloadState.percent / 100f,
                    )
                }

                is DownloadState.Error -> {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp),
                        text = stringResource(id = R.string.error_download_fail),
                    )
                }

                else -> Unit
            }
        }
    }

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
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = stringResource(id = R.string.hint_local_diffusion_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (buildInfoProvider.type == BuildType.FOSS) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    checked = state.localCustomModel,
                    onCheckedChange = onAllowLocalCustomModel,
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(id = R.string.model_local_custom_switch),
                )
            }
        }
        if (state.localCustomModel && buildInfoProvider.type == BuildType.FOSS) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = R.string.model_local_permission_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                onClick = launchManageStoragePermission,
            ) {
                Text(
                    text = stringResource(id = R.string.model_local_permission_button),
                    color = LocalContentColor.current,
                )
            }
        }
        state.localModels
            .filter {
                val customPredicate = it.id == LocalAiModel.CUSTOM.id
                if (state.localCustomModel) customPredicate else !customPredicate
            }
            .forEach { localModel -> modelItemUi(localModel) }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = R.string.hint_local_diffusion_warning),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ConfigurationModeButton(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    mode: ServerSource,
    onClick: (ServerSource) -> Unit = {},
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f),
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (state.mode == mode) MaterialTheme.colorScheme.primary
                else Color.Transparent,
            )
            .clickable { onClick(mode) },
    ) {
        Icon(
            modifier = Modifier
                .size(42.dp)
                .padding(top = 8.dp, bottom = 8.dp),
            imageVector = when (mode) {
                ServerSource.AUTOMATIC1111 -> Icons.Default.Computer
                ServerSource.HORDE,
                ServerSource.HUGGING_FACE -> Icons.Default.Cloud
                ServerSource.LOCAL -> Icons.Default.Android
                else -> Icons.Default.QuestionMark
            },
            contentDescription = null,
        )
        Text(
            modifier = Modifier
                .align(Alignment.CenterVertically)
                .padding(top = 8.dp, bottom = 8.dp),
            text = stringResource(id = when (mode) {
                ServerSource.AUTOMATIC1111 -> R.string.srv_type_own
                ServerSource.HORDE -> R.string.srv_type_horde
                ServerSource.LOCAL -> R.string.srv_type_local
                ServerSource.HUGGING_FACE -> R.string.srv_type_hugging_face
            }),
            fontSize = 14.sp,
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
        state = ServerSetupState(mode = ServerSource.AUTOMATIC1111),
        demoModeUrl = "https://sdai.moroz.cc",
    )
}
