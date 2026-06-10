package com.shifthackz.aisdv1.presentation.widget.item

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.Grid

/**
 * Renders the `GridIcon` UI for the SDAI presentation layer.
 *
 * @param grid grid value consumed by the API.
 * @param modifier Compose modifier applied to the rendered UI.
 * @param iconSize icon size value consumed by the API.
 * @param spacing spacing value consumed by the API.
 * @param color color value consumed by the API.
 * @author Dmitriy Moroz
 */
@Composable
fun GridIcon(
    grid: Grid,
    modifier: Modifier = Modifier,
    iconSize: Dp = 24.dp,
    spacing: Dp = 2.dp,
    color: Color = MaterialTheme.colorScheme.primary,
) {
    Column(
        modifier = modifier.size(iconSize),
        verticalArrangement = Arrangement.spacedBy(spacing),
    ) {
        repeat(grid.size) {
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(spacing),
            ) {
                repeat(grid.size) {
                    Box(
                        modifier = Modifier
                            .size(iconSize.div(grid.size))
                            .weight(1f)
                            .background(color, RoundedCornerShape(spacing)),
                    )
                }
            }
        }
    }
}
