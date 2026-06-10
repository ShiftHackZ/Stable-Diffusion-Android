package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.domain.preference.PreferenceManager

class DebugMenuAccessor(
    private val preferenceManager: PreferenceManager,
) {

    private var tapCount = 0

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

    private companion object {
        const val DEBUG_MENU_ACCESS_TAPS = 7
    }
}
