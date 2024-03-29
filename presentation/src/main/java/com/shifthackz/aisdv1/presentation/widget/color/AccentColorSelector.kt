package com.shifthackz.aisdv1.presentation.widget.color

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
import com.shifthackz.catppuccin.palette.Catppuccin
import com.shifthackz.catppuccin.palette.CatppuccinPalette

@Composable
@Preview
fun AccentColorSelector(
    modifier: Modifier = Modifier,
    palette: CatppuccinPalette = Catppuccin.Latte,
    selectedToken: ColorToken = ColorToken.MAUVE,
    onSelected: (Color, ColorToken) -> Unit = { _, _ -> },
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
        ) {
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.ROSEWATER,
                color = palette.Rosewater,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.FLAMINGO,
                color = palette.Flamingo,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.PINK,
                color = palette.Pink,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.MAUVE,
                color = palette.Mauve,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.RED,
                color = palette.Red,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.MAROON,
                color = palette.Maroon,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.PEACH,
                color = palette.Peach,
                onClick = onSelected,
            )
        }
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.YELLOW,
                color = palette.Yellow,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.GREEN,
                color = palette.Green,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.TEAL,
                color = palette.Teal,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.SKY,
                color = palette.Sky,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.SAPPHIRE,
                color = palette.Sapphire,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.BLUE,
                color = palette.Blue,
                onClick = onSelected,
            )
            ColorComposable(
                modifier = colorModifier,
                palette = palette,
                selectedToken = selectedToken,
                token = ColorToken.LAVENDER,
                color = palette.Lavender,
                onClick = onSelected,
            )
        }
    }
}
