@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.common.platform.Platform
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.presentation.generated.resources.Res
import com.shifthackz.aisdv1.presentation.generated.resources.sdai_logo
import com.shifthackz.aisdv1.presentation.model.readinessFor
import com.shifthackz.aisdv1.presentation.screen.setup.component.icon
import com.shifthackz.aisdv1.presentation.screen.setup.component.subtitle
import com.shifthackz.aisdv1.presentation.screen.setup.component.title
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi
import org.jetbrains.compose.resources.painterResource

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
    platform: Platform,
    strings: ServerSetupStrings,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(16.dp)
    val readiness = source.readinessFor(platform)
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
                SourceProviderIcon(
                    modifier = Modifier.size(42.dp),
                    source = source,
                    platform = platform,
                )
                Text(
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(vertical = 8.dp),
                    text = source.title(strings, platform),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
            Text(
                modifier = Modifier.padding(horizontal = 8.dp),
                text = source.subtitle(strings, platform),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.W500,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            FlowRow(
                modifier = Modifier.padding(4.dp),
            ) {
                SourceMetaChip(
                    text = readiness.mapToUi(strings),
                    containerColor = readiness.containerColor(),
                    contentColor = readiness.contentColor(),
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
private fun SourceProviderIcon(
    source: ServerSource,
    platform: Platform,
    modifier: Modifier = Modifier,
) {
    if (source == ServerSource.SDAI_CLOUD) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Surface(
                modifier = Modifier.size(30.dp),
                shape = RoundedCornerShape(7.dp),
                color = Color.White,
                tonalElevation = 1.dp,
            ) {
                Image(
                    modifier = Modifier
                        .size(30.dp)
                        .padding(2.dp)
                        .clip(RoundedCornerShape(7.dp)),
                    painter = painterResource(Res.drawable.sdai_logo),
                    contentDescription = null,
                )
            }
        }
    } else {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                modifier = Modifier.size(26.dp),
                imageVector = source.icon(platform),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
            )
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
