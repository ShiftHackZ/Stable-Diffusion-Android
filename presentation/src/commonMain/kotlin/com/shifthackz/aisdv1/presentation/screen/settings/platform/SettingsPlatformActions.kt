package com.shifthackz.aisdv1.presentation.screen.settings.platform

import androidx.compose.runtime.Composable

/**
 * Platform bridge for Settings actions that cannot be implemented in common code.
 *
 * Implementations request Android/iOS permissions, open external URLs or app settings,
 * and expose platform capability flags used to hide unsupported Settings rows.
 *
 * @author Dmitriy Moroz
 */
interface SettingsPlatformActions {
    /**
     * True when saving generated images through MediaStore still needs a runtime storage permission.
     *
     * @author Dmitriy Moroz
     */
    val requiresStoragePermissionForMediaStore: Boolean
    /**
     * True when this target can continue generation work after the app leaves foreground.
     *
     * @author Dmitriy Moroz
     */
    val supportsBackgroundGeneration: Boolean
    /**
     * Localization key for the warning shown under the background generation toggle.
     *
     * @author Dmitriy Moroz
     */
    val backgroundGenerationWarningKey: String

    suspend fun requestStoragePermission(): Boolean

    suspend fun requestNotificationPermission(): Boolean

    fun openUrl(url: String)

    /**
     * Opens the problem-report channel with diagnostic context prepared by the platform.
     *
     * @author Dmitriy Moroz
     */
    fun shareLogFile()

    fun showDeveloperModeUnlocked()

    fun openAppSettings()
}

/**
 * Common fallback used in previews/tests and unsupported targets.
 *
 * @author Dmitriy Moroz
 */
object NoOpSettingsPlatformActions : SettingsPlatformActions {
    override val requiresStoragePermissionForMediaStore = false
    override val supportsBackgroundGeneration = false
    override val backgroundGenerationWarningKey = "settings_item_background_generation_warning"

    override suspend fun requestStoragePermission(): Boolean = true

    override suspend fun requestNotificationPermission(): Boolean = true

    override fun openUrl(url: String) = Unit

    override fun shareLogFile() = Unit

    override fun showDeveloperModeUnlocked() = Unit

    override fun openAppSettings() = Unit
}

@Composable
expect fun rememberSettingsPlatformActions(): SettingsPlatformActions
