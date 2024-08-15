package com.shifthackz.aisdv1.presentation.theme.global

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.ui.MviComponent
import com.shifthackz.aisdv1.presentation.theme.colorTokenPalette
import com.shifthackz.catppuccin.compose.CatppuccinTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AiSdAppTheme(
    content: @Composable () -> Unit,
) {
    MviComponent(
        viewModel = koinViewModel<AiSdAppThemeViewModel>(),
        applySystemUiColors = false,
    ) { state, _ ->
        AiSdAppTheme(state, content)
    }
}

@Composable
fun AiSdAppTheme(
    state: AiSdAppThemeState,
    content: @Composable () -> Unit,
) {
    val context = LocalContext.current
    val isDark = if (state.systemDarkTheme) {
        isSystemInDarkTheme()
    } else {
        state.darkTheme
    }
    if (state.systemColorPalette && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        MaterialTheme(
            colorScheme = if (isDark) {
                dynamicDarkColorScheme(context)
            } else {
                dynamicLightColorScheme(context)
            },
            content = content,
        )
    } else {
        CatppuccinTheme.Palette(
            palette = colorTokenPalette(
                token = state.colorToken,
                darkThemeToken = state.darkThemeToken,
                isDark = isDark,
            ),
            content = content,
        )
    }
}
