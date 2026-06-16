@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.theme.global.persistentBottomBarWindowInsets
import com.shifthackz.aisdv1.presentation.theme.global.persistentTopAppBarWindowInsets

/**
 * Carries `ServerSetupStrings` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
data class ServerSetupStrings(
    /**
     * Exposes the `title` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val title: String = Localization.string("title_server_setup"),
    /**
     * Exposes the `next` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val next: String = Localization.string("next"),
    /**
     * Exposes the `connect` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val connect: String = Localization.string("action_connect"),
    /**
     * Exposes the `setup` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val setup: String = Localization.string("action_setup"),
    /**
     * Exposes the `backContentDescription` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backContentDescription: String = Localization.string("action_back"),
    /**
     * Exposes the `loadingConfiguration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val loadingConfiguration: String = Localization.string("splash_status_fetching"),
    /**
     * Exposes the `communicatingTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val communicatingTitle: String = Localization.string("communicating_progress_title"),
    /**
     * Exposes the `communicatingSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val communicatingSubtitle: String = Localization.string("communicating_progress_sub_title"),
    /**
     * Exposes the `errorTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val errorTitle: String = Localization.string("error_title"),
    /**
     * Exposes the `ok` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val ok: String = Localization.string("ok"),
    /**
     * Exposes the `cancel` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val cancel: String = Localization.string("cancel"),
    /**
     * Exposes the `connectLocalHostTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val connectLocalHostTitle: String = Localization.string("setup_localhost_url_title"),
    /**
     * Exposes the `connectLocalHostText` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val connectLocalHostText: String = Localization.string("setup_localhost_url_text"),
    /**
     * Exposes the `sourceTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sourceTitle: String = Localization.string("srv_step_1"),
    /**
     * Exposes the `configureTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val configureTitle: String = Localization.string("srv_step_2"),
    /**
     * Exposes the `sourceSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val sourceSubtitle: String = "",
    /**
     * Exposes the `serverUrl` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val serverUrl: String = Localization.string("hint_server_url"),
    /**
     * Exposes the `apiKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val apiKey: String = Localization.string("hint_server_horde_api_key"),
    /**
     * Exposes the `model` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val model: String = Localization.string("hint_hugging_face_model"),
    /**
     * Exposes the `authTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val authTitle: String = Localization.string("auth_title"),
    /**
     * Exposes the `anonymous` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val anonymous: String = Localization.string("auth_anonymous"),
    /**
     * Exposes the `httpBasic` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val httpBasic: String = Localization.string("auth_http_basic"),
    /**
     * Exposes the `login` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val login: String = Localization.string("hint_login"),
    /**
     * Exposes the `password` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val password: String = Localization.string("hint_password"),
    /**
     * Exposes the `showPassword` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val showPassword: String = Localization.string("action_show_password"),
    /**
     * Exposes the `hidePassword` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hidePassword: String = Localization.string("action_hide_password"),
    /**
     * Exposes the `instructions` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val instructions: String = Localization.string("settings_item_instructions"),
    /**
     * Exposes the `demoMode` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val demoMode: String = Localization.string("settings_item_demo"),
    /**
     * Exposes the `useDefaultHordeKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val useDefaultHordeKey: String = Localization.string("hint_server_horde_use_default_api_key"),
    /**
     * Exposes the `hordeAbout` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeAbout: String = Localization.string("hint_server_horde_about"),
    /**
     * Exposes the `hordeGetKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeGetKey: String = Localization.string("hint_server_horde_get_api_key"),
    /**
     * Exposes the `huggingFaceAbout` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceAbout: String = Localization.string("hint_hugging_face_about"),
    /**
     * Exposes the `openAiAbout` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiAbout: String = Localization.string("hint_open_ai_about"),
    /**
     * Exposes the `stabilityAbout` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityAbout: String = Localization.string("hint_stability_ai_about"),
    /**
     * Exposes the `automaticTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val automaticTitle: String = Localization.string("srv_type_own"),
    /**
     * Exposes the `automaticFormTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val automaticFormTitle: String = Localization.string("hint_server_setup_title"),
    /**
     * Exposes the `automaticSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val automaticSubtitle: String = Localization.string("hint_server_setup_sub_title"),
    /**
     * Exposes the `automaticHint` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val automaticHint: String = Localization.string("hint_args_warning"),
    /**
     * Exposes the `demoHint` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val demoHint: String = Localization.string("hint_demo_mode"),
    /**
     * Exposes the `swarmTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmTitle: String = Localization.string("srv_type_swarm_ui"),
    /**
     * Exposes the `swarmFormTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmFormTitle: String = Localization.string("hint_swarm_ui_title"),
    /**
     * Exposes the `swarmSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmSubtitle: String = Localization.string("hint_swarm_ui_sub_title"),
    /**
     * Exposes the `swarmHint` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmHint: String = Localization.string("hint_args_swarm_ui_warning"),
    /**
     * Exposes the `automaticValidUrls` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val automaticValidUrls: String = Localization.string("hint_valid_urls", "7860"),
    /**
     * Exposes the `swarmValidUrls` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val swarmValidUrls: String = Localization.string("hint_valid_urls", "7801"),
    /**
     * Exposes the `hordeUsage` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeUsage: String = Localization.string("hint_server_horde_usage"),
    /**
     * Exposes the `hordeTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeTitle: String = Localization.string("hint_server_horde_title"),
    /**
     * Exposes the `hordeSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val hordeSubtitle: String = Localization.string("hint_server_horde_sub_title"),
    /**
     * Exposes the `huggingFaceTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceTitle: String = Localization.string("hint_hugging_face_title"),
    /**
     * Exposes the `huggingFaceSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val huggingFaceSubtitle: String = Localization.string("hint_hugging_face_sub_title"),
    /**
     * Exposes the `openAiTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiTitle: String = Localization.string("hint_open_ai_title"),
    /**
     * Exposes the `openAiSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val openAiSubtitle: String = Localization.string("hint_open_ai_sub_title"),
    /**
     * Exposes the `stabilityTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilityTitle: String = Localization.string("hint_stability_ai_title"),
    /**
     * Exposes the `stabilitySubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stabilitySubtitle: String = Localization.string("hint_stability_ai_sub_title"),
    /**
     * Exposes the `localDiffusionTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionTitle: String = Localization.string("hint_local_diffusion_title"),
    /**
     * Exposes the `localDiffusionSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localDiffusionSubtitle: String = Localization.string("hint_local_diffusion_sub_title"),
    /**
     * Exposes the `mediaPipeTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mediaPipeTitle: String = Localization.string("hint_mediapipe_title"),
    /**
     * Exposes the `mediaPipeSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val mediaPipeSubtitle: String = Localization.string("hint_mediapipe_sub_title"),
    /**
     * Exposes the `localWarning` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localWarning: String = Localization.string("hint_local_diffusion_warning"),
    /**
     * Exposes the `localCustomSwitch` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCustomSwitch: String = Localization.string("model_local_custom_switch"),
    /**
     * Exposes the `localPermissionHeader` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPermissionHeader: String = Localization.string("model_local_permission_header"),
    /**
     * Exposes the `localPermissionTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPermissionTitle: String = Localization.string("model_local_permission_title"),
    /**
     * Exposes the `localPermissionButton` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPermissionButton: String = Localization.string("model_local_permission_button"),
    /**
     * Exposes the `localPathHeader` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPathHeader: String = Localization.string("model_local_path_header"),
    /**
     * Exposes the `localPathTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPathTitle: String = Localization.string("model_local_path_title"),
    /**
     * Exposes the `localPathButton` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localPathButton: String = Localization.string("model_local_path_button"),
    /**
     * Exposes the `localCustomTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCustomTitle: String = Localization.string("model_local_custom_title"),
    /**
     * Exposes the `localCustomSubtitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val localCustomSubtitle: String = Localization.string("model_local_custom_sub_title"),
    /**
     * Exposes the `download` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val download: String = Localization.string("download"),
    /**
     * Exposes the `delete` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val delete: String = Localization.string("delete"),
    /**
     * Exposes the `retry` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val retry: String = Localization.string("retry"),
    /**
     * Exposes the `downloadFailed` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val downloadFailed: String = Localization.string("error_download_fail"),
    /**
     * Exposes the `reset` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val reset: String = Localization.string("action_reset"),
    /**
     * Exposes the `deleteLocalModelTitle` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val deleteLocalModelTitle: String = Localization.string("interaction_delete_local_model_title"),
)

/**
 * Renders the `ServerSetupContent` UI for the SDAI presentation layer.
 *
 * @param state state rendered or processed by the component.
 * @param processIntent process intent value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param strings strings value consumed by the API.
 * @author Dmitriy Moroz
 */
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
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
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
                        windowInsets = persistentTopAppBarWindowInsets(),
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
                        .windowInsetsPadding(persistentBottomBarWindowInsets())
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
