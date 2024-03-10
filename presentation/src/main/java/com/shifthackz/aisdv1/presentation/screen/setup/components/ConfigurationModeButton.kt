@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.R
import com.shifthackz.aisdv1.presentation.screen.setup.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi

@Composable
fun ConfigurationModeButton(
    modifier: Modifier = Modifier,
    state: ServerSetupState,
    mode: ServerSource,
    onClick: (ServerSource) -> Unit = {},
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp),
            )
            .border(
                width = 2.dp,
                shape = RoundedCornerShape(16.dp),
                color = if (state.mode == mode) MaterialTheme.colorScheme.primary
                else Color.Transparent,
            )
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
                    ServerSource.AUTOMATIC1111 -> Icons.Default.Computer
                    ServerSource.HORDE,
                    ServerSource.OPEN_AI,
                    ServerSource.STABILITY_AI,
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
                    ServerSource.OPEN_AI -> R.string.srv_type_open_ai
                    ServerSource.STABILITY_AI -> R.string.srv_type_stability_ai
                }),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
            )
        }
        val descriptionId = when (mode) {
            ServerSource.AUTOMATIC1111 -> null
            ServerSource.HORDE -> R.string.hint_server_horde_sub_title
            ServerSource.HUGGING_FACE -> R.string.hint_hugging_face_sub_title
            ServerSource.OPEN_AI -> R.string.hint_open_ai_sub_title
            ServerSource.LOCAL -> R.string.hint_local_diffusion_sub_title
            ServerSource.STABILITY_AI -> R.string.hint_stability_ai_sub_title
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
