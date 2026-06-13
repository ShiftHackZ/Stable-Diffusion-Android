@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.component.icon
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import com.shifthackz.aisdv1.presentation.screen.setup.component.subtitle
import com.shifthackz.aisdv1.presentation.screen.setup.component.title

/**
 * Renders one provider option in the provider selection list.
 *
 * @param source provider represented by this row.
 * @param selected true when this provider is the current setup target.
 * @param strings localized labels used by source title/subtitle and metadata.
 * @param onClick invoked when the provider row is selected.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceModeItem(
    source: ServerSource,
    selected: Boolean,
    strings: ServerSetupStrings,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = if (selected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 4.dp),
        ) {
            Row {
                Icon(
                    modifier = Modifier
                        .size(42.dp)
                        .padding(top = 8.dp, bottom = 8.dp),
                    imageVector = source.icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 8.dp),
                    text = source.title(strings),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = source.subtitle(strings),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.padding(4.dp),
            ) {
                SourceMetaChip(
                    text = source.readiness.mapToUi(strings),
                    containerColor = source.readiness.containerColor(),
                    contentColor = source.readiness.contentColor(),
                )
                SourceMetaChip(
                    text = strings.sourceVersion(source.version),
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                )
                source.featureTags.forEach { tag ->
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
}

@Composable
private fun SourceMetaChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Text(
        modifier = Modifier
            .padding(4.dp)
            .background(
                color = containerColor,
                shape = RoundedCornerShape(4.dp),
            )
            .padding(horizontal = 4.dp, vertical = 2.dp),
        text = text,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.W500,
        color = contentColor,
    )
}
