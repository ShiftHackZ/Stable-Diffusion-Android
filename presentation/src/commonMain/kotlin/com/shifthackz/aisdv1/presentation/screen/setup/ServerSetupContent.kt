@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.DeveloperMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.modal.download.DownloadDialog
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.theme.textFieldColors
import com.shifthackz.aisdv1.presentation.widget.dialog.ErrorDialogContent
import com.shifthackz.aisdv1.presentation.widget.dialog.ProgressDialog
import com.shifthackz.aisdv1.presentation.widget.input.DropdownTextField
import com.shifthackz.aisdv1.presentation.widget.input.PlatformOutlinedTextField
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem
import com.shifthackz.aisdv1.presentation.widget.scrollbar.verticalScrollbar
import kotlinx.coroutines.launch

data class ServerSetupStrings(
    val title: String = Localization.string("title_server_setup"),
    val next: String = Localization.string("next"),
    val connect: String = Localization.string("action_connect"),
    val setup: String = Localization.string("action_setup"),
    val backContentDescription: String = Localization.string("action_back"),
    val loadingConfiguration: String = Localization.string("splash_status_fetching"),
    val communicatingTitle: String = Localization.string("communicating_progress_title"),
    val communicatingSubtitle: String = Localization.string("communicating_progress_sub_title"),
    val errorTitle: String = Localization.string("error_title"),
    val ok: String = Localization.string("ok"),
    val cancel: String = Localization.string("cancel"),
    val connectLocalHostTitle: String = Localization.string("setup_localhost_url_title"),
    val connectLocalHostText: String = Localization.string("setup_localhost_url_text"),
    val sourceTitle: String = Localization.string("srv_step_1"),
    val configureTitle: String = Localization.string("srv_step_2"),
    val sourceSubtitle: String = "",
    val serverUrl: String = Localization.string("hint_server_url"),
    val apiKey: String = Localization.string("hint_server_horde_api_key"),
    val model: String = Localization.string("hint_hugging_face_model"),
    val authTitle: String = Localization.string("auth_title"),
    val anonymous: String = Localization.string("auth_anonymous"),
    val httpBasic: String = Localization.string("auth_http_basic"),
    val login: String = Localization.string("hint_login"),
    val password: String = Localization.string("hint_password"),
    val showPassword: String = Localization.string("action_show_password"),
    val hidePassword: String = Localization.string("action_hide_password"),
    val instructions: String = Localization.string("settings_item_instructions"),
    val demoMode: String = Localization.string("settings_item_demo"),
    val useDefaultHordeKey: String = Localization.string("hint_server_horde_use_default_api_key"),
    val hordeAbout: String = Localization.string("hint_server_horde_about"),
    val hordeGetKey: String = Localization.string("hint_server_horde_get_api_key"),
    val automaticTitle: String = Localization.string("srv_type_own"),
    val automaticFormTitle: String = Localization.string("hint_server_setup_title"),
    val automaticSubtitle: String = Localization.string("hint_server_setup_sub_title"),
    val automaticHint: String = Localization.string("hint_args_warning"),
    val demoHint: String = Localization.string("hint_demo_mode"),
    val swarmTitle: String = Localization.string("srv_type_swarm_ui"),
    val swarmFormTitle: String = Localization.string("hint_swarm_ui_title"),
    val swarmSubtitle: String = Localization.string("hint_swarm_ui_sub_title"),
    val swarmHint: String = Localization.string("hint_args_swarm_ui_warning"),
    val automaticValidUrls: String = Localization.string("hint_valid_urls", "7860"),
    val swarmValidUrls: String = Localization.string("hint_valid_urls", "7801"),
    val hordeUsage: String = Localization.string("hint_server_horde_usage"),
    val hordeTitle: String = Localization.string("hint_server_horde_title"),
    val hordeSubtitle: String = Localization.string("hint_server_horde_sub_title"),
    val huggingFaceTitle: String = Localization.string("hint_hugging_face_title"),
    val huggingFaceSubtitle: String = Localization.string("hint_hugging_face_sub_title"),
    val openAiTitle: String = Localization.string("hint_open_ai_title"),
    val openAiSubtitle: String = Localization.string("hint_open_ai_sub_title"),
    val stabilityTitle: String = Localization.string("hint_stability_ai_title"),
    val stabilitySubtitle: String = Localization.string("hint_stability_ai_sub_title"),
    val localDiffusionTitle: String = Localization.string("hint_local_diffusion_title"),
    val localDiffusionSubtitle: String = Localization.string("hint_local_diffusion_sub_title"),
    val mediaPipeTitle: String = Localization.string("hint_mediapipe_title"),
    val mediaPipeSubtitle: String = Localization.string("hint_mediapipe_sub_title"),
    val localWarning: String = Localization.string("hint_local_diffusion_warning"),
    val localCustomSwitch: String = Localization.string("model_local_custom_switch"),
    val localPermissionHeader: String = Localization.string("model_local_permission_header"),
    val localPermissionTitle: String = Localization.string("model_local_permission_title"),
    val localPermissionButton: String = Localization.string("model_local_permission_button"),
    val localPathHeader: String = Localization.string("model_local_path_header"),
    val localPathTitle: String = Localization.string("model_local_path_title"),
    val localPathButton: String = Localization.string("model_local_path_button"),
    val localCustomTitle: String = Localization.string("model_local_custom_title"),
    val localCustomSubtitle: String = Localization.string("model_local_custom_sub_title"),
    val download: String = Localization.string("download"),
    val delete: String = Localization.string("delete"),
    val retry: String = Localization.string("retry"),
    val downloadFailed: String = Localization.string("error_download_fail"),
    val reset: String = Localization.string("action_reset"),
    val deleteLocalModelTitle: String = Localization.string("interaction_delete_local_model_title"),
)

@Composable
fun ServerSetupContent(
    state: ServerSetupState,
    processIntent: (ServerSetupIntent) -> Unit,
    modifier: Modifier = Modifier,
    strings: ServerSetupStrings = ServerSetupStrings(),
) {
    val sourceListState = rememberLazyListState()
    val configurationListState = rememberLazyListState()

    LaunchedEffect(state.mode) {
        configurationListState.scrollToItem(0)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                Column {
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = strings.title,
                                style = MaterialTheme.typography.headlineMedium,
                            )
                        },
                        navigationIcon = {
                            if (state.showBackNavArrow || state.step != ServerSetupState.Step.SOURCE) {
                                IconButton(
                                    onClick = { processIntent(ServerSetupIntent.NavigateBack) },
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                        contentDescription = strings.backContentDescription,
                                    )
                                }
                            }
                        },
                    )
                    ConfigurationStepBar(
                        currentStep = state.step,
                        strings = strings,
                    )
                }
            },
            bottomBar = {
                Button(
                    modifier = Modifier
                        .navigationBarsPadding()
                        .fillMaxWidth()
                        .height(68.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 16.dp, top = 8.dp),
                    enabled = !state.loadingConfiguration && state.mainButtonEnabled,
                    onClick = { processIntent(ServerSetupIntent.MainButtonClick) },
                ) {
                    Text(
                        text = when (state.step) {
                            ServerSetupState.Step.SOURCE -> strings.next
                            ServerSetupState.Step.CONFIGURE -> when (state.mode) {
                                ServerSource.LOCAL_MICROSOFT_ONNX,
                                ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
                                -> strings.setup

                                else -> strings.connect
                            }
                        },
                        color = LocalContentColor.current,
                    )
                }
            },
        ) { paddingValues ->
            if (state.loadingConfiguration) {
                ServerSetupLoading(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    text = strings.loadingConfiguration,
                )
            } else {
                when (state.step) {
                    ServerSetupState.Step.SOURCE -> SourceSelectionStep(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        state = state,
                        strings = strings,
                        listState = sourceListState,
                        processIntent = processIntent,
                    )

                    ServerSetupState.Step.CONFIGURE -> ConfigurationStep(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        state = state,
                        strings = strings,
                        listState = configurationListState,
                        processIntent = processIntent,
                    )
                }
            }
        }
        ServerSetupModal(
            modal = state.modal,
            strings = strings,
            processIntent = processIntent,
        )
    }
}
