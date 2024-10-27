@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.QuestionMark
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.widget.source.getName
import com.shifthackz.aisdv1.core.localization.R as LocalizationR

@Composable
fun ConfigurationModeButton(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    mode: ServerSource,
    onClick: (ServerSource) -> Unit = {},
) {
    val bgColor = MaterialTheme.colorScheme.surfaceVariant
    val borderColor = MaterialTheme.colorScheme.primary
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = bgColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                )
                if (state.mode != mode) return@drawBehind
                    drawRoundRect(
                        color = borderColor,
                        style = Stroke(2.dp.toPx()),
                        cornerRadius = CornerRadius(16.dp.toPx()),
                    )
            }
            .clickable { onClick(mode) }
            .padding(horizontal = 4.dp)
            .padding(bottom = 4.dp),
    ) {
        Row {
            Icon(
                modifier = Modifier
                    .size(42.dp)
                    .padding(top = 8.dp, bottom = 8.dp),
                imageVector = when (mode) {
                    ServerSource.AUTOMATIC1111,
                    ServerSource.SWARM_UI -> Icons.Default.Computer

                    ServerSource.HORDE,
                    ServerSource.OPEN_AI,
                    ServerSource.STABILITY_AI,
                    ServerSource.HUGGING_FACE -> Icons.Default.Cloud

                    ServerSource.LOCAL_MICROSOFT_ONNX,
                    ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> Icons.Default.Android

                    else -> Icons.Default.QuestionMark
                },
                contentDescription = null,
            )
            Text(
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .padding(vertical = 8.dp),
                text = mode.getName(),
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        val descriptionId = when (mode) {
            ServerSource.AUTOMATIC1111 -> LocalizationR.string.hint_server_setup_sub_title
            ServerSource.HORDE -> LocalizationR.string.hint_server_horde_sub_title
            ServerSource.HUGGING_FACE -> LocalizationR.string.hint_hugging_face_sub_title
            ServerSource.OPEN_AI -> LocalizationR.string.hint_open_ai_sub_title
            ServerSource.LOCAL_MICROSOFT_ONNX -> LocalizationR.string.hint_local_diffusion_sub_title
            ServerSource.STABILITY_AI -> LocalizationR.string.hint_stability_ai_sub_title
            ServerSource.SWARM_UI -> LocalizationR.string.hint_swarm_ui_sub_title
            ServerSource.LOCAL_GOOGLE_MEDIA_PIPE -> LocalizationR.string.hint_mediapipe_sub_title
            else -> null
        }
        descriptionId?.let { resId ->
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = stringResource(id = resId),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W500,
            )
        }
        FlowRow(
            modifier = Modifier.padding(4.dp),
        ) {
            mode.featureTags.forEach { tag ->
                Text(
                    modifier = Modifier
                        .padding(4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceTint,
                            shape = RoundedCornerShape(4.dp),
                        )
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                    text = tag.mapToUi(),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.W300,
                )
            }
        }
    }
}
