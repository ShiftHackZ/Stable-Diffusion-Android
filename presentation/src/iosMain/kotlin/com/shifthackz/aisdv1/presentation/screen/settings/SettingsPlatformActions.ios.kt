package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.presentation.platform.IosUrlOpener
import platform.UIKit.UIApplicationOpenSettingsURLString

@Composable
actual fun rememberSettingsPlatformActions(): SettingsPlatformActions = remember {
    IosSettingsPlatformActions
}

private object IosSettingsPlatformActions : SettingsPlatformActions {
    override val requiresStoragePermissionForMediaStore: Boolean = false
    override val supportsBackgroundGeneration: Boolean = false

    override suspend fun requestStoragePermission(): Boolean = true

    override suspend fun requestNotificationPermission(): Boolean = true

    override fun openUrl(url: String) {
        IosUrlOpener.openUrl(url)
    }

    override fun shareLogFile() {
        IosUrlOpener.openUrl(REPORT_PROBLEM_MAILTO)
    }

    override fun showDeveloperModeUnlocked() = Unit

    override fun openAppSettings() {
        IosUrlOpener.openUrl(UIApplicationOpenSettingsURLString)
    }

    private const val REPORT_PROBLEM_MAILTO =
        "mailto:sdai@moroz.cc?subject=SDAI%20-%20Problem%20report&body=SDAI%20iOS"
}
