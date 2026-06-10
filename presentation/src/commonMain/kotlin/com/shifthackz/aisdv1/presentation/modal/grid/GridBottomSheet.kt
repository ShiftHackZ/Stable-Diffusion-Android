package com.shifthackz.aisdv1.presentation.modal.grid

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalContentColor
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.core.model.asUiText
import com.shifthackz.aisdv1.domain.entity.Grid
import com.shifthackz.aisdv1.presentation.widget.item.GridIcon
import com.shifthackz.aisdv1.presentation.widget.item.SettingsItem

@Composable
fun GridBottomSheet(
    modifier: Modifier = Modifier,
    currentGrid: Grid = Grid.Fixed2,
    onSelected: (Grid) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .navigationBarsPadding()
            .padding(bottom = 16.dp),
    ) {
        Grid.entries.forEach { grid ->
            val textCount = Localization.string(
                when (grid) {
                    Grid.Fixed2 -> "two"
                    Grid.Fixed3 -> "three"
                    Grid.Fixed4 -> "four"
                    Grid.Fixed5 -> "five"
                },
            )
            SettingsItem(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                selected = grid == currentGrid,
                text = "$textCount (${grid.size})".asUiText(),
                showChevron = false,
                onClick = { onSelected(grid) },
                startIconContent = {
                    GridIcon(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        grid = grid,
                        color = LocalContentColor.current,
                    )
                }
            )
        }
    }
}
