package com.shifthackz.aisdv1.presentation.widget.color

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.presentation.theme.toColor
import com.shifthackz.catppuccin.palette.Catppuccin

@Composable
@Preview
fun DarkThemeColorSelector(
    modifier: Modifier = Modifier,
    selectedToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    colorToken: ColorToken = ColorToken.MAUVE,
    onSelected: (Color, DarkThemeToken) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier) {
        val rowModifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp)
        val colorModifier = Modifier
            .weight(1f)
            .size(32.dp)
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            listOf(
                Catppuccin.Frappe to DarkThemeToken.FRAPPE,
                Catppuccin.Macchiato to DarkThemeToken.MACCHIATO,
                Catppuccin.Mocha to DarkThemeToken.MOCHA,
            ).forEach { (palette, token) ->
                ColorComposable(
                    modifier = colorModifier,
                    palette = palette,
                    selectedToken = selectedToken,
                    token = token,
                    color = palette.Base,
                    onClick = onSelected,
                    iconTint = colorToken.toColor(palette),
                    selectedBorderTint = colorToken.toColor(palette).copy(alpha = 0.4f),
                    unselectedBorderTint = palette.Overlay2.copy(alpha = 0.3f),
                    borderSize = 2.dp,
                )
            }
        }
    }
}
