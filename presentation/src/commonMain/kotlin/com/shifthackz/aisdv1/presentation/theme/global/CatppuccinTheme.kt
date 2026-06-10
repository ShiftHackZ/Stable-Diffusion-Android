package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken

internal fun catppuccinColorScheme(
    token: ColorToken,
    darkThemeToken: DarkThemeToken,
    isDark: Boolean,
) = catppuccinPalette(isDark, darkThemeToken).let { palette ->
    val primary = token.toColor(palette)
    val secondary = primary.copy(alpha = 0.5f)
    if (isDark) {
        darkColorScheme(
            primary = primary,
            onPrimary = palette.base,
            primaryContainer = primary,
            onPrimaryContainer = palette.base,
            inversePrimary = primary.inverse(),
            secondary = secondary,
            onSecondary = palette.mantle,
            secondaryContainer = secondary,
            onSecondaryContainer = palette.mantle,
            tertiary = secondary,
            onTertiary = palette.base,
            tertiaryContainer = secondary,
            onTertiaryContainer = palette.base,
            background = palette.base,
            onBackground = palette.text,
            surface = palette.mantle,
            onSurface = palette.mantle.inverse(),
            surfaceVariant = palette.mantle,
            onSurfaceVariant = palette.mantle.inverse(),
            surfaceTint = palette.crust,
            inverseSurface = palette.mantle.inverse(),
            inverseOnSurface = palette.mantle,
            error = palette.red,
            onError = palette.red.inverse(),
            errorContainer = palette.rosewater,
            onErrorContainer = palette.rosewater.inverse(),
            outline = primary,
            outlineVariant = secondary,
            scrim = palette.crust,
        )
    } else {
        lightColorScheme(
            primary = primary,
            onPrimary = palette.base,
            primaryContainer = primary,
            onPrimaryContainer = palette.base,
            inversePrimary = primary.inverse(),
            secondary = secondary,
            onSecondary = palette.mantle,
            secondaryContainer = secondary,
            onSecondaryContainer = palette.mantle,
            tertiary = secondary,
            onTertiary = palette.base,
            tertiaryContainer = secondary,
            onTertiaryContainer = palette.base,
            background = palette.base,
            onBackground = palette.text,
            surface = palette.mantle,
            onSurface = palette.mantle.inverse(),
            surfaceVariant = palette.mantle,
            onSurfaceVariant = palette.mantle.inverse(),
            surfaceTint = palette.crust,
            inverseSurface = palette.mantle.inverse(),
            inverseOnSurface = palette.mantle,
            error = palette.red,
            onError = palette.red.inverse(),
            errorContainer = palette.rosewater,
            onErrorContainer = palette.rosewater.inverse(),
            outline = primary,
            outlineVariant = secondary,
            scrim = palette.crust,
        )
    }
}

internal fun catppuccinTypography(
    isDark: Boolean,
    darkThemeToken: DarkThemeToken,
): Typography = Typography().let { typography ->
    typography.copy(
        displayLarge = typography.displayLarge.copy(color = Color.Unspecified),
        displayMedium = typography.displayMedium.copy(color = Color.Unspecified),
        displaySmall = typography.displaySmall.copy(color = Color.Unspecified),
        headlineLarge = typography.headlineLarge.copy(color = Color.Unspecified),
        headlineMedium = typography.headlineMedium.copy(color = Color.Unspecified),
        headlineSmall = typography.headlineSmall.copy(color = Color.Unspecified),
        titleLarge = typography.titleLarge.copy(color = Color.Unspecified),
        titleMedium = typography.titleMedium.copy(color = Color.Unspecified),
        titleSmall = typography.titleSmall.copy(color = Color.Unspecified),
        bodyLarge = typography.bodyLarge.copy(color = Color.Unspecified),
        bodyMedium = typography.bodyMedium.copy(color = Color.Unspecified),
        bodySmall = typography.bodySmall.copy(color = Color.Unspecified),
        labelLarge = typography.labelLarge.copy(color = Color.Unspecified),
        labelMedium = typography.labelMedium.copy(color = Color.Unspecified),
        labelSmall = typography.labelSmall.copy(color = Color.Unspecified),
    )
}

private fun catppuccinPalette(
    isDark: Boolean,
    darkThemeToken: DarkThemeToken,
): CatppuccinPalette = if (!isDark) {
    CatppuccinPalette.Latte
} else {
    when (darkThemeToken) {
        DarkThemeToken.FRAPPE -> CatppuccinPalette.Frappe
        DarkThemeToken.MACCHIATO -> CatppuccinPalette.Macchiato
        DarkThemeToken.MOCHA -> CatppuccinPalette.Mocha
    }
}

internal fun catppuccinAccentColor(
    token: ColorToken,
    isDark: Boolean,
    darkThemeToken: DarkThemeToken,
): Color = token.toColor(catppuccinPalette(isDark, darkThemeToken))

internal fun catppuccinBaseColor(
    isDark: Boolean,
    darkThemeToken: DarkThemeToken,
): Color = catppuccinPalette(isDark, darkThemeToken).base

internal fun catppuccinOverlayColor(
    isDark: Boolean,
    darkThemeToken: DarkThemeToken,
): Color = catppuccinPalette(isDark, darkThemeToken).overlay1

private fun ColorToken.toColor(palette: CatppuccinPalette): Color =
    when (this) {
        ColorToken.ROSEWATER -> palette.rosewater
        ColorToken.FLAMINGO -> palette.flamingo
        ColorToken.PINK -> palette.pink
        ColorToken.MAUVE -> palette.mauve
        ColorToken.RED -> palette.red
        ColorToken.MAROON -> palette.maroon
        ColorToken.PEACH -> palette.peach
        ColorToken.YELLOW -> palette.yellow
        ColorToken.GREEN -> palette.green
        ColorToken.TEAL -> palette.teal
        ColorToken.SKY -> palette.sky
        ColorToken.SAPPHIRE -> palette.sapphire
        ColorToken.BLUE -> palette.blue
        ColorToken.LAVENDER -> palette.lavender
    }

private fun Color.inverse(): Color =
    Color(
        red = 1f - red,
        green = 1f - green,
        blue = 1f - blue,
        alpha = alpha,
    )

private data class CatppuccinPalette(
    val rosewater: Color,
    val flamingo: Color,
    val pink: Color,
    val mauve: Color,
    val red: Color,
    val maroon: Color,
    val peach: Color,
    val yellow: Color,
    val green: Color,
    val teal: Color,
    val sky: Color,
    val sapphire: Color,
    val blue: Color,
    val lavender: Color,
    val text: Color,
    val subtext1: Color,
    val subtext0: Color,
    val overlay1: Color,
    val base: Color,
    val mantle: Color,
    val crust: Color,
) {

    companion object {
        val Latte = CatppuccinPalette(
            rosewater = Color(0xffdc8a78),
            flamingo = Color(0xffdd7878),
            pink = Color(0xffea76cb),
            mauve = Color(0xff8839ef),
            red = Color(0xffd20f39),
            maroon = Color(0xffe64553),
            peach = Color(0xfffe640b),
            yellow = Color(0xffdf8e1d),
            green = Color(0xff40a02b),
            teal = Color(0xff179299),
            sky = Color(0xff04a5e5),
            sapphire = Color(0xff209fb5),
            blue = Color(0xff1e66f5),
            lavender = Color(0xff7287fd),
            text = Color(0xff4c4f69),
            subtext1 = Color(0xff5c5f77),
            subtext0 = Color(0xff6c6f85),
            overlay1 = Color(0xff8c8fa1),
            base = Color(0xffeff1f5),
            mantle = Color(0xffe6e9ef),
            crust = Color(0xffdce0e8),
        )

        val Frappe = CatppuccinPalette(
            rosewater = Color(0xfff2d5cf),
            flamingo = Color(0xffeebebe),
            pink = Color(0xfff4b8e4),
            mauve = Color(0xffca9ee6),
            red = Color(0xffe78284),
            maroon = Color(0xffea999c),
            peach = Color(0xffef9f76),
            yellow = Color(0xffe5c890),
            green = Color(0xffa6d189),
            teal = Color(0xff81c8be),
            sky = Color(0xff99d1db),
            sapphire = Color(0xff85c1dc),
            blue = Color(0xff8caaee),
            lavender = Color(0xffbabbf1),
            text = Color(0xffc6d0f5),
            subtext1 = Color(0xffb5bfe2),
            subtext0 = Color(0xffa5adce),
            overlay1 = Color(0xff838ba7),
            base = Color(0xff303446),
            mantle = Color(0xff292c3c),
            crust = Color(0xff232634),
        )

        val Macchiato = CatppuccinPalette(
            rosewater = Color(0xfff4dbd6),
            flamingo = Color(0xfff0c6c6),
            pink = Color(0xfff5bde6),
            mauve = Color(0xffc6a0f6),
            red = Color(0xffed8796),
            maroon = Color(0xffee99a0),
            peach = Color(0xfff5a97f),
            yellow = Color(0xffeed49f),
            green = Color(0xffa6da95),
            teal = Color(0xff8bd5ca),
            sky = Color(0xff91d7e3),
            sapphire = Color(0xff7dc4e4),
            blue = Color(0xff8aadf4),
            lavender = Color(0xffb7bdf8),
            text = Color(0xffcad3f5),
            subtext1 = Color(0xffb8c0e0),
            subtext0 = Color(0xffa5adcb),
            overlay1 = Color(0xff8087a2),
            base = Color(0xff24273a),
            mantle = Color(0xff1e2030),
            crust = Color(0xff181926),
        )

        val Mocha = CatppuccinPalette(
            rosewater = Color(0xfff5e0dc),
            flamingo = Color(0xfff2cdcd),
            pink = Color(0xfff5c2e7),
            mauve = Color(0xffcba6f7),
            red = Color(0xfff38ba8),
            maroon = Color(0xffeba0ac),
            peach = Color(0xfffab387),
            yellow = Color(0xfff9e2af),
            green = Color(0xffa6e3a1),
            teal = Color(0xff94e2d5),
            sky = Color(0xff89dceb),
            sapphire = Color(0xff74c7ec),
            blue = Color(0xff89b4fa),
            lavender = Color(0xffb4befe),
            text = Color(0xffcdd6f4),
            subtext1 = Color(0xffbac2de),
            subtext0 = Color(0xffa6adc8),
            overlay1 = Color(0xff7f849c),
            base = Color(0xff1e1e2e),
            mantle = Color(0xff181825),
            crust = Color(0xff11111b),
        )
    }
}
