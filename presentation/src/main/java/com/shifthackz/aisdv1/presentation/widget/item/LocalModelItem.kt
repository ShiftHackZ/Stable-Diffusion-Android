package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.FileDownloadDone
import androidx.compose.material.icons.outlined.FileDownloadOff
import androidx.compose.material.icons.outlined.Landslide
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState

@Composable
fun LocalModelItem(
    modifier: Modifier = Modifier,
    model: ServerSetupState.LocalModel,
    onDownloadCardButtonClick: (ServerSetupState.LocalModel) -> Unit = {},
    onSelect: (ServerSetupState.LocalModel) -> Unit = {}
) {
    Column(
        modifier = modifier
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
            .clickable { onSelect(model) },
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

                fun folderModifier(treeNum: Int) = Modifier.padding(start = (treeNum - 1) * 12.dp)
                val folderStyle = MaterialTheme.typography.bodySmall
                Text(
                    modifier = folderModifier(1),
                    text = "Download",
                    style = folderStyle,
                )
                Text(
                    modifier = folderModifier(2),
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
                    text = "tokenizer_config.json",
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
