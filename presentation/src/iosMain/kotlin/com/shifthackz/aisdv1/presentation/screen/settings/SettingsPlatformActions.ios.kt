package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.shifthackz.aisdv1.presentation.platform.IosUrlOpener
import platform.UIKit.UIApplicationOpenSettingsURLString

/**
 * Renders the `rememberSettingsPlatformActions` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberSettingsPlatformActions`.
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberSettingsPlatformActions(): SettingsPlatformActions = remember {
    IosSettingsPlatformActions
}

/**
 * Provides the `IosSettingsPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private object IosSettingsPlatformActions : SettingsPlatformActions {
    /**
     * Exposes the `requiresStoragePermissionForMediaStore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val requiresStoragePermissionForMediaStore: Boolean = false
    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = true
    /**
     * Exposes the `backgroundGenerationWarningKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val backgroundGenerationWarningKey = "settings_item_background_generation_warning_ios"

    /**
     * Executes the `requestStoragePermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestStoragePermission`.
     * @author Dmitriy Moroz
     */
    override suspend fun requestStoragePermission(): Boolean = true

    /**
     * Executes the `requestNotificationPermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestNotificationPermission`.
     * @author Dmitriy Moroz
     */
    override suspend fun requestNotificationPermission(): Boolean = true

    /**
     * Executes the `openUrl` step in the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun openUrl(url: String) {
        IosUrlOpener.openUrl(url)
    }

    /**
     * Performs the SDAI side effect handled by `shareLogFile`.
     *
     * @author Dmitriy Moroz
     */
    override fun shareLogFile() {
        IosUrlOpener.openUrl(REPORT_PROBLEM_MAILTO)
    }

    /**
     * Executes the `showDeveloperModeUnlocked` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showDeveloperModeUnlocked() = Unit

    /**
     * Executes the `openAppSettings` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun openAppSettings() {
        IosUrlOpener.openUrl(UIApplicationOpenSettingsURLString)
    }

    /**
     * Exposes the `REPORT_PROBLEM_MAILTO` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private const val REPORT_PROBLEM_MAILTO =
        "mailto:sdai@moroz.cc?subject=SDAI%20-%20Problem%20report&body=SDAI%20iOS"
}
