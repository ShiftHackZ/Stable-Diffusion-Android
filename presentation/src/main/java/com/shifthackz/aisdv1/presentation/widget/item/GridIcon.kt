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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.Grid

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

@Composable
@Preview
private fun GridIconsPreview2() {
    GridIcon(Grid.Fixed2)
}

@Composable
@Preview
private fun GridIconsPreview3() {
    GridIcon(Grid.Fixed3)
}

@Composable
@Preview
private fun GridIconsPreview4() {
    GridIcon(Grid.Fixed4)
}

@Composable
@Preview
private fun GridIconsPreview5() {
    GridIcon(Grid.Fixed5)
}
