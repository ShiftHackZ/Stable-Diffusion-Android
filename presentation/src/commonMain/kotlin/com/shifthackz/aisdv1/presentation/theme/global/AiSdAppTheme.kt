package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.core.mvi.MviComponent
import com.shifthackz.aisdv1.presentation.di.initKoin

@Composable
fun AiSdAppTheme(
    content: @Composable () -> Unit,
) {
    val koin = remember { initKoin() }
    val viewModel = remember(koin) {
        koin.get<AiSdAppThemeViewModel>()
    }
    MviComponent(viewModel = viewModel) { state ->
        AiSdAppTheme(state = state, content = content)
    }
}

@Composable
fun AiSdAppTheme(
    state: AiSdAppThemeState,
    applySystemBars: Boolean = true,
    content: @Composable () -> Unit,
) {
    val isDark = if (state.systemDarkTheme) {
        isSystemInDarkTheme()
    } else {
        state.darkTheme
    }
    val dynamicColorScheme = dynamicColorSchemeOrNull(
        useDynamicColors = state.systemColorPalette,
        isDark = isDark,
    )
    MaterialTheme(
        colorScheme = dynamicColorScheme ?: catppuccinColorScheme(
            token = state.colorToken,
            darkThemeToken = state.darkThemeToken,
            isDark = isDark,
        ),
        typography = catppuccinTypography(isDark, state.darkThemeToken),
    ) {
        if (applySystemBars) {
            ApplySystemBarTheme(
                colorScheme = MaterialTheme.colorScheme,
                isDark = isDark,
            )
        }
        content()
    }
}
