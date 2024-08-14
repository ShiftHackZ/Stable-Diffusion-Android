package com.shifthackz.aisdv1.presentation.screen.setup.forms

import android.content.Intent
import android.provider.DocumentsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.core.common.appbuild.BuildType
import com.shifthackz.aisdv1.core.common.file.LOCAL_DIFFUSION_CUSTOM_PATH
import com.shifthackz.aisdv1.core.extensions.getRealPath
import com.shifthackz.aisdv1.core.model.asString
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupScreenTags.CUSTOM_MODEL_SWITCH
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun LocalDiffusionForm(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    buildInfoProvider: BuildInfoProvider = BuildInfoProvider.stub,
    processIntent: (ServerSetupIntent) -> Unit = {},
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
                .clickable { processIntent(ServerSetupIntent.SelectLocalModel(model)) },
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = 4.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
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
                        .padding(start = 8.dp)
                        .size(48.dp),
                    imageVector = icon,
                    contentDescription = "Download state",
                )
                Column(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .weight(1f)
                ) {
                    Text(
                        text = model.name,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 2
                    )
                    if (model.id != LocalAiModel.CUSTOM.id) {
                        Text(
                            text = model.size,
                            maxLines = 1
                        )
                    }
                }
                if (model.id != LocalAiModel.CUSTOM.id) {
                    Button(
                        modifier = Modifier.padding(end = 8.dp),
                        onClick = { processIntent(ServerSetupIntent.LocalModel.ClickReduce(model)) },
                    ) {
                        Text(
                            text = stringResource(
                                id = when (model.downloadState) {
                                    is DownloadState.Downloading -> LocalizationR.string.cancel
                                    is DownloadState.Error -> LocalizationR.string.retry
                                    else -> {
                                        if (model.downloaded) LocalizationR.string.delete
                                        else LocalizationR.string.download
                                    }
                                }
                            ),
                            color = LocalContentColor.current,
                            maxLines = 1
                        )
                    }
                }
            }
            if (model.id == LocalAiModel.CUSTOM.id) {
                Column(
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text(
                        text = stringResource(id = LocalizationR.string.model_local_custom_title),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(id = LocalizationR.string.model_local_custom_sub_title),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    fun folderModifier(treeNum: Int) =
                        Modifier.padding(start = (treeNum - 1) * 12.dp)

                    val folderStyle = MaterialTheme.typography.bodySmall
                    Text(
                        modifier = Modifier.padding(start = 12.dp),
                        text = state.localCustomModelPath,
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(3),
                        text = "text_encoder",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "model.ort",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(3),
                        text = "tokenizer",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "merges.txt",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(3),
                        text = "special_tokens_map.json",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "tokenizer_config.json",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "vocab.json",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(3),
                        text = "unet",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "model.ort",
                        style = folderStyle,
                    )

                    Text(
                        modifier = folderModifier(3),
                        text = "vae_decoder",
                        style = folderStyle,
                    )
                    Text(
                        modifier = folderModifier(4),
                        text = "model.ort",
                        style = folderStyle,
                    )
                }
            }
            when (model.downloadState) {
                is DownloadState.Downloading -> {
                    LinearProgressIndicator(
                        progress = { model.downloadState.percent / 100f },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
                    )
                }

                is DownloadState.Error -> {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .padding(bottom = 8.dp),
                        text = stringResource(id = LocalizationR.string.error_download_fail),
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
            text = stringResource(id = LocalizationR.string.hint_local_diffusion_title),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier = Modifier.padding(top = 16.dp, bottom = 16.dp),
            text = stringResource(id = LocalizationR.string.hint_local_diffusion_sub_title),
            style = MaterialTheme.typography.bodyMedium,
        )
        if (buildInfoProvider.type == BuildType.FOSS) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Switch(
                    modifier = Modifier.testTag(CUSTOM_MODEL_SWITCH),
                    checked = state.localCustomModel,
                    onCheckedChange = {
                        processIntent(ServerSetupIntent.AllowLocalCustomModel(it))
                    },
                )
                Text(
                    modifier = Modifier.padding(start = 8.dp),
                    text = stringResource(id = LocalizationR.string.model_local_custom_switch),
                )
            }
        }
        if (state.localCustomModel && buildInfoProvider.type == BuildType.FOSS) {
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                text = stringResource(id = LocalizationR.string.model_local_permission_header),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = stringResource(id = LocalizationR.string.model_local_permission_title),
                style = MaterialTheme.typography.bodyMedium,
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(vertical = 8.dp),
                onClick = { processIntent(ServerSetupIntent.LaunchManageStoragePermission) },
            ) {
                Text(
                    text = stringResource(id = LocalizationR.string.model_local_permission_button),
                    color = LocalContentColor.current,
                )
            }
            Text(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 8.dp),
                text = stringResource(id = LocalizationR.string.model_local_path_header),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )
            val context = LocalContext.current
            val uriFlags =
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartActivityForResult()
            ) { result ->
                result.data?.data?.let { uri ->
                    context.contentResolver.takePersistableUriPermission(uri, uriFlags)
                    val docUri = DocumentsContract.buildDocumentUriUsingTree(
                        uri,
                        DocumentsContract.getTreeDocumentId(uri)
                    )
                    getRealPath(context, docUri)
                        ?.let(ServerSetupIntent::SelectLocalModelPath)
                        ?.let(processIntent::invoke)
                }
            }
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 14.dp),
                value = state.localCustomModelPath,
                onValueChange = { processIntent(ServerSetupIntent.SelectLocalModelPath(it)) },
                enabled = true,
                singleLine = true,
                label = { Text(stringResource(LocalizationR.string.model_local_path_title)) },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            processIntent(
                                ServerSetupIntent.SelectLocalModelPath(LOCAL_DIFFUSION_CUSTOM_PATH)
                            )
                        },
                        content = {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                            )
                        },
                    )
                },
                isError = state.localCustomModelPathValidationError != null,
                supportingText = {
                    state.localCustomModelPathValidationError
                        ?.let { Text(it.asString(), color = MaterialTheme.colorScheme.error) }
                },
            )
            OutlinedButton(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp, bottom = 8.dp),
                onClick = {
                    val intent = Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
                        addFlags(uriFlags)
                    }
                    launcher.launch(intent)
                },
            ) {
                Text(
                    text = stringResource(id = LocalizationR.string.model_local_path_button),
                    color = LocalContentColor.current,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
        state.localModels
            .filter {
                val customPredicate = it.id == LocalAiModel.CUSTOM.id
                if (state.localCustomModel) customPredicate else !customPredicate
            }
            .forEach { localModel -> modelItemUi(localModel) }
        Text(
            modifier = Modifier.padding(top = 16.dp),
            text = stringResource(id = LocalizationR.string.hint_local_diffusion_warning),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}
