@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.DownloadState
import com.shifthackz.aisdv1.domain.entity.LocalAiModel


/**
 * Renders the `LocalModelItem` UI for the SDAI presentation layer.
 *
 * @param model model value consumed by the API.
 * @param selected selected value consumed by the API.
 * @param strings strings value consumed by the API.
 * @param onSelect callback invoked by the component.
 * @param onAction callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
internal fun LocalModelItem(
    model: ServerSetupState.LocalModel,
    selected: Boolean,
    strings: ServerSetupStrings,
    onSelect: () -> Unit,
    onAction: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .clickable(onClick = onSelect),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceTint.copy(alpha = 0.8f),
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            BorderStroke(2.dp, Color.Transparent)
        },
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LocalModelStatusIcon(model = model)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.name,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.W600,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    if (!model.isCustom) {
                        Text(
                            text = model.size,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                }
                if (!model.isCustom) {
                    LocalModelActionButton(
                        model = model,
                        strings = strings,
                        onClick = onAction,
                    )
                }
            }
            if (model.isCustom) {
                Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = strings.localCustomTitle,
                    style = MaterialTheme.typography.bodyMedium,
                )
                if (model.id == LocalAiModel.CustomOnnx.id) {
                    Text(
                        modifier = Modifier.padding(top = 4.dp),
                        text = strings.localCustomSubtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    LocalOnnxFolderStructure()
                }
            }
            when (val downloadState = model.downloadState) {
                is DownloadState.Downloading -> LinearProgressIndicator(
                    progress = { downloadState.percent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )

                is DownloadState.Error -> Text(
                    modifier = Modifier.padding(top = 8.dp),
                    text = strings.downloadFailed,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )

                else -> Unit
            }
        }
    }
}

/**
 * Renders the `LocalModelStatusIcon` UI for the SDAI presentation layer.
 *
 * @param model model value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
private fun LocalModelStatusIcon(model: ServerSetupState.LocalModel) {
    val foregroundColor = if (model.downloaded) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = Modifier.size(44.dp),
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.matchParentSize(),
            shape = RoundedCornerShape(12.dp),
            color = Color.Transparent,
            border = BorderStroke(1.dp, foregroundColor.copy(alpha = 0.48f)),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    modifier = Modifier.size(26.dp),
                    imageVector = Icons.Outlined.AutoAwesome,
                    contentDescription = null,
                    tint = foregroundColor,
                )
            }
        }
        if (model.downloaded) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 3.dp, y = 3.dp)
                    .size(18.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceTint),
            ) {
                Icon(
                    modifier = Modifier.padding(3.dp),
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                )
            }
        }
    }
}

/**
 * Renders the `LocalModelActionButton` UI for the SDAI presentation layer.
 *
 * @param model model value consumed by the API.
 * @param strings strings value consumed by the API.
 * @param onClick callback invoked by the component.
 * @author Dmitriy Moroz
 */
@Composable
private fun LocalModelActionButton(
    model: ServerSetupState.LocalModel,
    strings: ServerSetupStrings,
    onClick: () -> Unit,
) {
    val action = model.action(strings)

    OutlinedIconButton(
        modifier = Modifier.size(40.dp),
        onClick = onClick,
    ) {
        Icon(
            modifier = Modifier.size(20.dp),
            imageVector = action.icon,
            contentDescription = action.label,
        )
    }
}

/**
 * Executes the `action` step in the SDAI presentation layer.
 *
 * @param strings strings value consumed by the API.
 * @return Result produced by `action`.
 * @author Dmitriy Moroz
 */
private fun ServerSetupState.LocalModel.action(strings: ServerSetupStrings): LocalModelAction =
    when (downloadState) {
        is DownloadState.Downloading -> LocalModelAction(
            icon = Icons.Outlined.Close,
            label = strings.cancel,
        )

        is DownloadState.Error -> LocalModelAction(
            icon = Icons.Outlined.Download,
            label = strings.retry,
        )

        else -> if (downloaded) {
            LocalModelAction(
                icon = Icons.Outlined.Delete,
                label = strings.delete,
            )
        } else {
            LocalModelAction(
                icon = Icons.Outlined.Download,
                label = strings.download,
            )
        }
    }

/**
 * Carries `LocalModelAction` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private data class LocalModelAction(
    /**
     * Exposes the `icon` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val icon: ImageVector,
    /**
     * Exposes the `label` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val label: String,
)

/**
 * Renders the `LocalOnnxFolderStructure` UI for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Composable
internal fun LocalOnnxFolderStructure() {
    val lines = listOf(
        1 to "text_encoder",
        2 to "model.ort",
        1 to "tokenizer",
        2 to "merges.txt",
        2 to "special_tokens_map.json",
        2 to "tokenizer_config.json",
        2 to "vocab.json",
        1 to "unet",
        2 to "model.ort",
        1 to "vae_decoder",
        2 to "model.ort",
    )
    lines.forEach { (level, name) ->
        Text(
            modifier = Modifier.padding(start = (level * 12).dp, top = 2.dp),
            text = name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
