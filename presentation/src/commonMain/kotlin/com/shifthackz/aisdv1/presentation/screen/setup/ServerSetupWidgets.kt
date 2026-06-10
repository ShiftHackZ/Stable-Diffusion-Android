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


@Composable
internal fun SettingsAction(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    SettingsItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        startIcon = icon,
        text = text.asUiText(),
        onClick = onClick,
    )
}

@Composable
internal fun SwitchRow(
    text: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    SettingsItem(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        startIcon = icon,
        text = text.asUiText(),
        showChevron = false,
        onClick = { onCheckedChange(!checked) },
        endValueContent = {
            Switch(
                modifier = Modifier.padding(horizontal = 8.dp),
                checked = checked,
                onCheckedChange = onCheckedChange,
            )
        },
    )
}

@Composable
internal fun ServerSetupModal(
    modal: ServerSetupState.Modal,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    when (modal) {
        ServerSetupState.Modal.None -> Unit
        ServerSetupState.Modal.Communicating -> ProgressDialog(
            title = strings.communicatingTitle.asUiText(),
            subTitle = strings.communicatingSubtitle.asUiText(),
            onDismissRequest = {},
        )

        ServerSetupState.Modal.ConnectLocalHost -> AlertDialog(
            onDismissRequest = {
                processIntent(ServerSetupIntent.DismissDialog)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        processIntent(ServerSetupIntent.ConnectToLocalHost)
                    },
                ) {
                    Text(text = strings.connect)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        processIntent(ServerSetupIntent.DismissDialog)
                    },
                ) {
                    Text(text = strings.cancel)
                }
            },
            title = { Text(text = strings.connectLocalHostTitle) },
            text = { Text(text = strings.connectLocalHostText) },
        )

        is ServerSetupState.Modal.Error -> ErrorDialogContent(
            title = strings.errorTitle,
            text = modal.message,
            okText = strings.ok,
            onDismissRequest = {
                processIntent(ServerSetupIntent.DismissDialog)
            },
        )

        is ServerSetupState.Modal.DeleteLocalModelConfirm -> AlertDialog(
            onDismissRequest = {
                processIntent(ServerSetupIntent.DismissDialog)
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        processIntent(ServerSetupIntent.LocalModel.DeleteConfirm(modal.model))
                    },
                ) {
                    Text(text = Localization.string("yes"))
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        processIntent(ServerSetupIntent.DismissDialog)
                    },
                ) {
                    Text(text = Localization.string("no"))
                }
            },
            title = { Text(text = strings.deleteLocalModelTitle) },
            text = {
                Text(
                    text = Localization.string(
                        "interaction_delete_local_model_sub_title",
                        modal.model.name,
                    ),
                )
            },
        )

        is ServerSetupState.Modal.SelectDownloadSource -> DownloadDialog(
            modelId = modal.modelId,
            onDismissRequest = {
                processIntent(ServerSetupIntent.DismissDialog)
            },
            onDownloadSourceSelected = { url ->
                processIntent(ServerSetupIntent.LocalModel.DownloadConfirm(modal.modelId, url))
            },
        )
    }
}

internal val ServerSource.icon: ImageVector
    get() = when (this) {
        ServerSource.AUTOMATIC1111,
        ServerSource.SWARM_UI,
        -> Icons.Default.Computer

        ServerSource.HORDE,
        ServerSource.HUGGING_FACE,
        ServerSource.OPEN_AI,
        ServerSource.STABILITY_AI,
        -> Icons.Default.Cloud

        ServerSource.LOCAL_MICROSOFT_ONNX,
        ServerSource.LOCAL_GOOGLE_MEDIA_PIPE,
        -> Icons.Default.Android
    }

internal fun ServerSource.title(strings: ServerSetupStrings): String = when (this) {
    ServerSource.AUTOMATIC1111 -> strings.automaticTitle
    ServerSource.SWARM_UI -> strings.swarmTitle
    ServerSource.HORDE -> strings.hordeTitle
    ServerSource.HUGGING_FACE -> strings.huggingFaceTitle
    ServerSource.OPEN_AI -> strings.openAiTitle
    ServerSource.STABILITY_AI -> strings.stabilityTitle
    ServerSource.LOCAL_MICROSOFT_ONNX -> strings.localDiffusionTitle
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> strings.mediaPipeTitle
}

internal fun ServerSource.subtitle(strings: ServerSetupStrings): String = when (this) {
    ServerSource.AUTOMATIC1111 -> strings.automaticSubtitle
    ServerSource.SWARM_UI -> strings.swarmSubtitle
    ServerSource.HORDE -> strings.hordeSubtitle
    ServerSource.HUGGING_FACE -> strings.huggingFaceSubtitle
    ServerSource.OPEN_AI -> strings.openAiSubtitle
    ServerSource.STABILITY_AI -> strings.stabilitySubtitle
    ServerSource.LOCAL_MICROSOFT_ONNX -> strings.localDiffusionSubtitle
    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> strings.mediaPipeSubtitle
}

internal fun ServerSetupState.ValidationError.message(
    strings: ServerSetupStrings,
): String = when (this) {
    ServerSetupState.ValidationError.EmptyField -> Localization.string("error_empty_field")
    ServerSetupState.ValidationError.EmptyUrl -> Localization.string("error_empty_url")
    ServerSetupState.ValidationError.InvalidScheme -> Localization.string("error_invalid_scheme")
    ServerSetupState.ValidationError.InvalidPort -> Localization.string("error_invalid_port")
    ServerSetupState.ValidationError.InvalidUrl -> Localization.string("error_invalid_url")
}

internal val ServerSetupState.mainButtonEnabled: Boolean
    get() = when (step) {
        ServerSetupState.Step.SOURCE -> true
        ServerSetupState.Step.CONFIGURE -> when (mode) {
            ServerSource.LOCAL_MICROSOFT_ONNX -> localOnnxCustomModel ||
                localOnnxModels.any { it.selected && it.downloaded }

            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> localMediaPipeCustomModel ||
                localMediaPipeModels.any { it.selected && it.downloaded }

            else -> true
        }
    }

internal val ServerSetupState.LocalModel.isCustom: Boolean
    get() = id == LocalAiModel.CustomOnnx.id || id == LocalAiModel.CustomMediaPipe.id

internal val ServerSetupState.LocalModel.downloadIcon: ImageVector
    get() = when (downloadState) {
        is DownloadState.Downloading -> Icons.Outlined.FileDownload
        else -> when {
            isCustom -> Icons.Outlined.Landslide
            downloaded -> Icons.Outlined.FileDownloadDone
            else -> Icons.Outlined.FileDownloadOff
        }
    }
