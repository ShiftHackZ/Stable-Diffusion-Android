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
import androidx.compose.ui.unit.dp
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken
import com.shifthackz.aisdv1.presentation.theme.global.catppuccinAccentColor
import com.shifthackz.aisdv1.presentation.theme.global.catppuccinBaseColor

@Composable
fun AccentColorSelector(
    modifier: Modifier = Modifier,
    isDark: Boolean = false,
    darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
    selectedToken: ColorToken = ColorToken.MAUVE,
    onSelected: (Color, ColorToken) -> Unit = { _, _ -> },
) {
    Column(modifier = modifier) {
        val iconTint = catppuccinBaseColor(isDark, darkThemeToken)
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
                selectedToken = selectedToken,
                token = ColorToken.ROSEWATER,
                color = catppuccinAccentColor(ColorToken.ROSEWATER, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.FLAMINGO,
                color = catppuccinAccentColor(ColorToken.FLAMINGO, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.PINK,
                color = catppuccinAccentColor(ColorToken.PINK, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.MAUVE,
                color = catppuccinAccentColor(ColorToken.MAUVE, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.RED,
                color = catppuccinAccentColor(ColorToken.RED, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.MAROON,
                color = catppuccinAccentColor(ColorToken.MAROON, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.PEACH,
                color = catppuccinAccentColor(ColorToken.PEACH, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
        }
        Row(
            modifier = rowModifier,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.YELLOW,
                color = catppuccinAccentColor(ColorToken.YELLOW, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.GREEN,
                color = catppuccinAccentColor(ColorToken.GREEN, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.TEAL,
                color = catppuccinAccentColor(ColorToken.TEAL, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.SKY,
                color = catppuccinAccentColor(ColorToken.SKY, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.SAPPHIRE,
                color = catppuccinAccentColor(ColorToken.SAPPHIRE, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.BLUE,
                color = catppuccinAccentColor(ColorToken.BLUE, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
            ColorComposable(
                modifier = colorModifier,
                selectedToken = selectedToken,
                token = ColorToken.LAVENDER,
                color = catppuccinAccentColor(ColorToken.LAVENDER, isDark, darkThemeToken),
                onClick = onSelected,
                iconTint = iconTint,
            )
        }
    }
}
