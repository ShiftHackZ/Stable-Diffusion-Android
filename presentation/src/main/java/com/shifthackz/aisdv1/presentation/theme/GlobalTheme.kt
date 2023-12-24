package com.shifthackz.aisdv1.presentation.theme

import androidx.compose.runtime.Composable
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import com.shifthackz.catppuccin.palette.Catppuccin

@Composable
fun AiStableDiffusionAppTheme(
    content: @Composable () -> Unit,
) {
    return CatppuccinTheme.DarkLightPalette(
        darkPalette = CatppuccinMaterial.Frappe(
            primary = Catppuccin.Frappe.Mauve,
            secondary = Catppuccin.Frappe.Mauve.copy(alpha = 0.5f),
            tertiary = Catppuccin.Frappe.Mauve.copy(alpha = 0.5f),
        ),
        lightPalette = CatppuccinMaterial.Latte(
            primary = Catppuccin.Latte.Mauve,
            secondary = Catppuccin.Latte.Mauve.copy(alpha = 0.5f),
            tertiary = Catppuccin.Latte.Mauve.copy(alpha = 0.5f),
        ),
        content = content,
    )
}
