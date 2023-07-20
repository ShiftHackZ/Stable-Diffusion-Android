package com.shifthackz.aisdv1.presentation.screen.debug

import com.shifthackz.aisdv1.core.common.appbuild.BuildInfoProvider
import com.shifthackz.aisdv1.presentation.utils.Constants

class DebugMenuAccessor(private val buildInfoProvider: BuildInfoProvider) {

    private var tapCount = 0;

    operator fun invoke(): Boolean {
        if (buildInfoProvider.isDebug) {
            tapCount++
            if (tapCount >= Constants.DEBUG_MENU_ACCESS_TAPS) {
                tapCount = 0;
                return true
            }
        }
        return false
    }
}
