package com.shifthackz.aisdv1.presentation.screen.setup.source

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupIntent
import com.shifthackz.aisdv1.presentation.screen.setup.model.ServerSetupState
import com.shifthackz.aisdv1.presentation.screen.setup.content.ServerSetupStrings

/**
 * Compact toolbar for provider search, filter, and sort actions.
 *
 * @param state current query/filter state used to show active indicators.
 * @param strings localized hints and action labels.
 * @param processIntent sends toolbar events to the setup MVI pipeline.
 * @author Dmitriy Moroz
 */
@Composable
internal fun SourceSelectionToolbar(
    state: ServerSetupState,
    strings: ServerSetupStrings,
    processIntent: (ServerSetupIntent) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(start = 16.dp, top = 2.dp, end = 8.dp, bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CompactSourceSearchField(
            modifier = Modifier
                .weight(1f)
                .height(40.dp),
            value = state.sourceSearchQuery,
            placeholder = strings.sourceSearchHint,
            onValueChange = { query ->
                processIntent(ServerSetupIntent.UpdateSourceSearchQuery(query))
            },
        )
        if (state.sourceSearchQuery.isNotBlank()) {
            ToolbarIconButton(
                icon = Icons.Default.Close,
                contentDescription = strings.clear,
                showDot = false,
                onClick = {
                    processIntent(ServerSetupIntent.UpdateSourceSearchQuery(""))
                },
            )
        }
        ToolbarIconButton(
            icon = Icons.Default.FilterList,
            contentDescription = strings.sourceFilterAction,
            showDot = state.sourceFiltersActive,
            onClick = { processIntent(ServerSetupIntent.ShowSourceFilters) },
        )
        ToolbarIconButton(
            contentDescription = strings.sourceSortAction,
            showDot = false,
            iconContent = { SourceSortToolbarIcon() },
            onClick = { processIntent(ServerSetupIntent.ShowSourceSort) },
        )
    }
}

@Composable
private fun CompactSourceSearchField(
    value: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                modifier = Modifier.size(18.dp),
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            BasicTextField(
                modifier = Modifier.weight(1f),
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                decorationBox = { innerTextField ->
                    Box(contentAlignment = Alignment.CenterStart) {
                        if (value.isBlank()) {
                            Text(
                                text = placeholder,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                            )
                        }
                        innerTextField()
                    }
                },
            )
        }
    }
}

@Composable
private fun ToolbarIconButton(
    contentDescription: String,
    showDot: Boolean,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconContent: (@Composable () -> Unit)? = null,
) {
    IconButton(onClick = onClick) {
        Box(contentAlignment = Alignment.TopEnd) {
            iconContent?.invoke() ?: icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = contentDescription,
                )
            }
            if (showDot) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = CircleShape,
                        ),
                )
            }
        }
    }
}

@Composable
private fun SourceSortToolbarIcon() {
    Icon(
        modifier = Modifier.size(24.dp),
        imageVector = MobileDataArrowsIcon,
        contentDescription = null,
    )
}

private val ServerSetupState.sourceFiltersActive: Boolean
    get() = sourceTypeFilter != null || sourceReadinessFilters.isNotEmpty() || sourceTagFilters.isNotEmpty()

private val MobileDataArrowsIcon: ImageVector
    get() {
        if (_mobileDataArrowsIcon != null) return _mobileDataArrowsIcon!!
        _mobileDataArrowsIcon = ImageVector.Builder(
            name = "MobileDataArrows",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f,
        ).apply {
            path(
                fill = null,
                stroke = SolidColor(Color.Black),
                strokeLineWidth = 2.4f,
                strokeLineCap = StrokeCap.Round,
                strokeLineJoin = StrokeJoin.Round,
            ) {
                moveTo(8f, 19.5f)
                lineTo(8f, 4.5f)
                moveTo(8f, 4.5f)
                lineTo(4.8f, 7.7f)
                moveTo(8f, 4.5f)
                lineTo(11.2f, 7.7f)
                moveTo(16f, 4.5f)
                lineTo(16f, 19.5f)
                moveTo(16f, 19.5f)
                lineTo(12.8f, 16.3f)
                moveTo(16f, 19.5f)
                lineTo(19.2f, 16.3f)
            }
        }.build()
        return _mobileDataArrowsIcon!!
    }

private var _mobileDataArrowsIcon: ImageVector? = null
