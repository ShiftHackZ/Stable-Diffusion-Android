package com.shifthackz.aisdv1.presentation.theme.global

import androidx.compose.runtime.Immutable
import com.shifthackz.aisdv1.core.mvi.MviState
import com.shifthackz.aisdv1.domain.entity.ColorToken
import com.shifthackz.aisdv1.domain.entity.DarkThemeToken

/**
 * Carries `AiSdAppThemeState` data through the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
@Immutable
data class AiSdAppThemeState(
    /**
     * Exposes the `stateKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val stateKey: Long = 0L,
    /**
     * Exposes the `systemColorPalette` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val systemColorPalette: Boolean = false,
    /**
     * Exposes the `systemDarkTheme` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val systemDarkTheme: Boolean = true,
    /**
     * Exposes the `darkTheme` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val darkTheme: Boolean = true,
    /**
     * Exposes the `colorToken` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val colorToken: ColorToken = ColorToken.MAUVE,
    /**
     * Exposes the `darkThemeToken` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val darkThemeToken: DarkThemeToken = DarkThemeToken.FRAPPE,
) : MviState
