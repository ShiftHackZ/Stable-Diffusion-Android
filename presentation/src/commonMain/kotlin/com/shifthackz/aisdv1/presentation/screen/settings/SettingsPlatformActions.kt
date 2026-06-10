package com.shifthackz.aisdv1.presentation.screen.settings

import androidx.compose.runtime.Composable

interface SettingsPlatformActions {
    val requiresStoragePermissionForMediaStore: Boolean
    val supportsBackgroundGeneration: Boolean

    suspend fun requestStoragePermission(): Boolean

    suspend fun requestNotificationPermission(): Boolean

    fun openUrl(url: String)

    fun shareLogFile()

    fun showDeveloperModeUnlocked()

    fun openAppSettings()
}

object NoOpSettingsPlatformActions : SettingsPlatformActions {
    override val requiresStoragePermissionForMediaStore = false
    override val supportsBackgroundGeneration = false

    override suspend fun requestStoragePermission(): Boolean = true

    override suspend fun requestNotificationPermission(): Boolean = true

    override fun openUrl(url: String) = Unit

    override fun shareLogFile() = Unit

    override fun showDeveloperModeUnlocked() = Unit

    override fun openAppSettings() = Unit
}

@Composable
expect fun rememberSettingsPlatformActions(): SettingsPlatformActions
