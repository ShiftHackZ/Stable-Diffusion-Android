package com.shifthackz.aisdv1.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.preference.PreferenceManager
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.palette.Catppuccin
import com.shifthackz.catppuccin.palette.CatppuccinPalette
import org.koin.compose.koinInject

@Composable
fun colors(
    light: Color,
    dark: Color,
): Color {
    return if (isSdAppInDarkTheme()) dark else light
}

@Composable
fun isSdAppInDarkTheme(): Boolean {
    val preferenceManager: PreferenceManager = koinInject()
    return if (preferenceManager.designUseSystemDarkTheme) {
        isSystemInDarkTheme()
    } else {
        preferenceManager.designDarkTheme
    }
}

@Composable
fun colorTokenPalette(
    token: ColorToken = ColorToken.MAUVE,
    isDark: Boolean,
): CatppuccinPalette {
    return if (isDark) {
        CatppuccinMaterial.Frappe(
            primary = token.toColor(Catppuccin.Frappe),
            secondary = token.toColor(Catppuccin.Frappe).copy(alpha = 0.5f),
            tertiary = token.toColor(Catppuccin.Frappe).copy(alpha = 0.5f),
        )
    } else {
        CatppuccinMaterial.Latte(
            primary = token.toColor(Catppuccin.Latte),
            secondary = token.toColor(Catppuccin.Latte).copy(alpha = 0.5f),
            tertiary = token.toColor(Catppuccin.Latte).copy(alpha = 0.5f),
        )
    }
}

fun ColorToken.toColor(palette: CatppuccinPalette): Color {
    return when (this) {
        ColorToken.ROSEWATER -> palette.Rosewater
        ColorToken.FLAMINGO -> palette.Flamingo
        ColorToken.PINK -> palette.Pink
        ColorToken.MAUVE -> palette.Mauve
        ColorToken.RED -> palette.Red
        ColorToken.MAROON -> palette.Maroon
        ColorToken.PEACH -> palette.Peach
        ColorToken.YELLOW -> palette.Yellow
        ColorToken.GREEN -> palette.Green
        ColorToken.TEAL -> palette.Teal
        ColorToken.SKY -> palette.Sky
        ColorToken.SAPPHIRE -> palette.Sapphire
        ColorToken.BLUE -> palette.Blue
        ColorToken.LAVENDER -> palette.Lavender
    }
}
