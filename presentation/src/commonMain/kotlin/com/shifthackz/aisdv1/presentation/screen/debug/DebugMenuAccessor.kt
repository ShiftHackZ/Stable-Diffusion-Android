package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.domain.preference.PreferenceManager

/**
 * Coordinates `DebugMenuAccessor` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
class DebugMenuAccessor(
    /**
     * Exposes the `preferenceManager` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val preferenceManager: PreferenceManager,
) {

    /**
     * Exposes the `tapCount` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private var tapCount = 0

    /**
     * Executes the `invoke` step in the SDAI presentation layer.
     *
     * @return Result produced by `invoke`.
     * @author Dmitriy Moroz
     */
    operator fun invoke(): Boolean {
        if (preferenceManager.developerMode) {
            return true
        }
        tapCount++
        if (tapCount >= DEBUG_MENU_ACCESS_TAPS) {
            tapCount = 0
            preferenceManager.developerMode = true
            return true
        }
        return false
    }

    /**
     * Provides the `companion object` singleton used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private companion object {
        /**
         * Exposes the `DEBUG_MENU_ACCESS_TAPS` value used by the SDAI presentation layer.
         *
         * @author Dmitriy Moroz
         */
        const val DEBUG_MENU_ACCESS_TAPS = 7
    }
}
