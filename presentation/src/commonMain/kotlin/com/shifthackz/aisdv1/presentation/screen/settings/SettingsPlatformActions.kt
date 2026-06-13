package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Composable

/**
 * Defines the `SettingsPlatformActions` contract for the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
interface SettingsPlatformActions {
    /**
     * Exposes the `requiresStoragePermissionForMediaStore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val requiresStoragePermissionForMediaStore: Boolean
    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val supportsBackgroundGeneration: Boolean
    /**
     * Exposes the `backgroundGenerationWarningKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    val backgroundGenerationWarningKey: String

    /**
     * Executes the `requestStoragePermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestStoragePermission`.
     * @author Dmitriy Moroz
     */
    suspend fun requestStoragePermission(): Boolean

    /**
     * Executes the `requestNotificationPermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestNotificationPermission`.
     * @author Dmitriy Moroz
     */
    suspend fun requestNotificationPermission(): Boolean

    /**
     * Executes the `openUrl` step in the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    fun openUrl(url: String)

    /**
     * Performs the SDAI side effect handled by `shareLogFile`.
     *
     * @author Dmitriy Moroz
     */
    fun shareLogFile()

    /**
     * Executes the `showDeveloperModeUnlocked` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun showDeveloperModeUnlocked()

    /**
     * Executes the `openAppSettings` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    fun openAppSettings()
}

/**
 * Provides the `NoOpSettingsPlatformActions` singleton used by the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
object NoOpSettingsPlatformActions : SettingsPlatformActions {
    /**
     * Exposes the `requiresStoragePermissionForMediaStore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val requiresStoragePermissionForMediaStore = false
    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration = false
    /**
     * Exposes the `backgroundGenerationWarningKey` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val backgroundGenerationWarningKey = "settings_item_background_generation_warning"

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
    override fun openUrl(url: String) = Unit

    /**
     * Performs the SDAI side effect handled by `shareLogFile`.
     *
     * @author Dmitriy Moroz
     */
    override fun shareLogFile() = Unit

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
    override fun openAppSettings() = Unit
}

/**
 * Renders the `rememberSettingsPlatformActions` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberSettingsPlatformActions`.
 * @author Dmitriy Moroz
 */
@Composable
expect fun rememberSettingsPlatformActions(): SettingsPlatformActions
