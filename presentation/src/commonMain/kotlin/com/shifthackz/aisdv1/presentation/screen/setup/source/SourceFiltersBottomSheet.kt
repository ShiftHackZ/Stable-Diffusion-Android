@file:OptIn(ExperimentalLayoutApi::class)

package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.FeatureTag
import com.shifthackz.aisdv1.domain.entity.ServerSource
import com.shifthackz.aisdv1.domain.entity.ServerSourceReadiness
import com.shifthackz.aisdv1.domain.entity.ServerSourceType
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings
import com.shifthackz.aisdv1.presentation.screen.setup.mappers.mapToUi

/**
 * Bottom sheet for provider type, readiness, and capability tag filters.
 *
 * Disabled chips represent impossible set intersections, so users can see
 * available providers without creating an empty result set by accident.
 *
 * @param state current filter state and available providers.
 * @param strings localized filter labels.
 * @param processIntent sends filter changes back to the setup MVI pipeline.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceFiltersBottomSheet(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    val sheetPadding = 16.dp
    val horizontalItemSpacing = 8.dp
    val verticalItemSpacing = 0.dp
    val availableTags = state.allowedModes
        .flatMap(ServerSource::featureTags)
        .distinct()
        .sortedBy(FeatureTag::ordinal)
    val availableReadiness = state.allowedModes
        .map(ServerSource::readiness)
        .distinct()
        .sortedBy(ServerSourceReadiness::ordinal)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .navigationBarsPadding()
            .padding(
                start = sheetPadding,
                top = sheetPadding,
                end = sheetPadding,
                bottom = sheetPadding,
            ),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(horizontalItemSpacing),
        ) {
            Text(
                text = strings.sourceOptionsTitle,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalItemSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalItemSpacing),
            ) {
                SourceTypeChip(
                    label = strings.sourceFilterAll,
                    selected = state.sourceTypeFilter == null,
                    onClick = {
                        processIntent(ServerSetupIntent.UpdateSourceTypeFilter(null))
                    },
                )
                ServerSourceType.entries.forEach { type ->
                    SourceTypeChip(
                        label = type.mapToUi(strings),
                        selected = state.sourceTypeFilter == type,
                        enabled = state.isSourceTypeEnabled(type),
                        onClick = {
                            processIntent(ServerSetupIntent.UpdateSourceTypeFilter(type))
                        },
                    )
                }
            }
            Text(
                text = strings.sourceFilterReadiness,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalItemSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalItemSpacing),
            ) {
                availableReadiness.forEach { readiness ->
                    SourceTypeChip(
                        label = readiness.mapToUi(strings),
                        selected = readiness in state.sourceReadinessFilters,
                        enabled = state.isSourceReadinessEnabled(readiness),
                        onClick = {
                            processIntent(ServerSetupIntent.ToggleSourceReadinessFilter(readiness))
                        },
                    )
                }
            }
            Text(
                text = strings.sourceFilterTags,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
            )
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(horizontalItemSpacing),
                verticalArrangement = Arrangement.spacedBy(verticalItemSpacing),
            ) {
                availableTags.forEach { tag ->
                    SourceTypeChip(
                        label = tag.mapToUi(),
                        selected = tag in state.sourceTagFilters,
                        enabled = state.isSourceTagEnabled(tag),
                        onClick = {
                            processIntent(ServerSetupIntent.ToggleSourceTagFilter(tag))
                        },
                    )
                }
            }
        }
        OutlinedButton(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = horizontalItemSpacing),
            enabled = state.sourceTypeFilter != null ||
                state.sourceReadinessFilters.isNotEmpty() ||
                state.sourceTagFilters.isNotEmpty(),
            onClick = {
                processIntent(ServerSetupIntent.ResetSourceFilters)
            },
        ) {
            Text(text = strings.reset)
        }
    }
}

@Composable
private fun SourceTypeChip(
    label: String,
    selected: Boolean,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(8.dp)
    val containerColor = when {
        selected -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }
    val contentColor = when {
        !enabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        selected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurface
    }
    val borderColor = when {
        !enabled -> MaterialTheme.colorScheme.primary.copy(alpha = 0.48f)
        else -> MaterialTheme.colorScheme.primary
    }
    Surface(
        shape = shape,
        color = containerColor,
        contentColor = contentColor,
        border = BorderStroke(1.dp, borderColor),
        enabled = enabled,
        onClick = onClick,
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            text = label,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}
