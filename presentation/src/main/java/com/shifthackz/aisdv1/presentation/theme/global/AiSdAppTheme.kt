package com.shifthackz.aisdv1.presentation.theme.global

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.catppuccin.compose.CatppuccinMaterial
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import com.shifthackz.catppuccin.palette.Catppuccin
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiSdAppTheme(
    content: @Composable () -> Unit,
) {
    MviComponent(
        viewModel = koinViewModel<AiSdAppThemeViewModel>(),
        applySystemUiColors = false,
    ) { state, _ ->
        val context = LocalContext.current
        val isDark = if (state.useSystemDarkTheme) {
            isSystemInDarkTheme()
        } else {
            state.useDarkTheme
        }
        if (state.useSystemColorPalette && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MaterialTheme(
                colorScheme = if (isDark) {
                    dynamicDarkColorScheme(context)
                } else {
                    dynamicLightColorScheme(context)
                },
                content = content,
            )
        } else {
            val palette = if (isDark) {
                CatppuccinMaterial.Frappe(
                    primary = Catppuccin.Frappe.Mauve,
                    secondary = Catppuccin.Frappe.Mauve.copy(alpha = 0.5f),
                    tertiary = Catppuccin.Frappe.Mauve.copy(alpha = 0.5f),
                )
            } else {
                CatppuccinMaterial.Latte(
                    primary = Catppuccin.Latte.Mauve,
                    secondary = Catppuccin.Latte.Mauve.copy(alpha = 0.5f),
                    tertiary = Catppuccin.Latte.Mauve.copy(alpha = 0.5f),
                )
            }
            CatppuccinTheme.Palette(
                palette = palette,
                content = content,
            )
        }
    }
}
