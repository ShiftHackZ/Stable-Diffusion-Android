package com.shifthackz.aisdv1.presentation.screen.settings

import android.content.Context
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.shifthackz.aisdv1.core.common.extensions.openAppSettings
import com.shifthackz.aisdv1.core.common.extensions.openUrl
import com.shifthackz.aisdv1.core.common.extensions.showToast
import com.shifthackz.aisdv1.core.localization.Localization
import com.shifthackz.aisdv1.presentation.utils.PermissionUtil
import com.shifthackz.aisdv1.presentation.utils.ReportProblemEmailComposer
import kotlinx.coroutines.CompletableDeferred

/**
 * Renders the `rememberSettingsPlatformActions` UI for the SDAI presentation layer.
 *
 * @return Result produced by `rememberSettingsPlatformActions`.
 * @author Dmitriy Moroz
 */
@Composable
actual fun rememberSettingsPlatformActions(): SettingsPlatformActions {
    val context = LocalContext.current
    val storagePermissionResult = remember { mutableStateOf<CompletableDeferred<Boolean>?>(null) }
    val notificationPermissionResult = remember { mutableStateOf<CompletableDeferred<Boolean>?>(null) }
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { result ->
        storagePermissionResult.value?.complete(!result.values.any { !it })
        storagePermissionResult.value = null
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        notificationPermissionResult.value?.complete(granted)
        notificationPermissionResult.value = null
    }

    return remember(context, storagePermissionLauncher, notificationPermissionLauncher) {
        AndroidSettingsPlatformActions(
            context = context,
            launchStoragePermission = storagePermissionLauncher::launch,
            launchNotificationPermission = notificationPermissionLauncher::launch,
            storagePermissionResult = storagePermissionResult,
            notificationPermissionResult = notificationPermissionResult,
        )
    }
}

/**
 * Coordinates `AndroidSettingsPlatformActions` behavior in the SDAI presentation layer.
 *
 * @author Dmitriy Moroz
 */
private class AndroidSettingsPlatformActions(
    /**
     * Exposes the `context` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val context: Context,
    /**
     * Exposes the `launchStoragePermission` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val launchStoragePermission: (Array<String>) -> Unit,
    /**
     * Exposes the `launchNotificationPermission` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val launchNotificationPermission: (String) -> Unit,
    /**
     * Exposes the `storagePermissionResult` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val storagePermissionResult: MutableState<CompletableDeferred<Boolean>?>,
    /**
     * Exposes the `notificationPermissionResult` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    private val notificationPermissionResult: MutableState<CompletableDeferred<Boolean>?>,
) : SettingsPlatformActions {

    /**
     * Exposes the `requiresStoragePermissionForMediaStore` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val requiresStoragePermissionForMediaStore: Boolean
        get() = Build.VERSION.SDK_INT < Build.VERSION_CODES.S_V2

    /**
     * Exposes the `supportsBackgroundGeneration` value used by the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override val supportsBackgroundGeneration: Boolean = true

    /**
     * Executes the `requestStoragePermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestStoragePermission`.
     * @author Dmitriy Moroz
     */
    override suspend fun requestStoragePermission(): Boolean {
        if (PermissionUtil.checkStoragePermission(context)) return true

        val result = CompletableDeferred<Boolean>()
        storagePermissionResult.value = result
        var launched = false
        val granted = PermissionUtil.checkStoragePermission(context) { permissions ->
            launched = true
            launchStoragePermission(permissions)
        }
        if (granted) {
            storagePermissionResult.value = null
            return true
        }
        if (!launched) {
            storagePermissionResult.value = null
            return false
        }
        return result.await()
    }

    /**
     * Executes the `requestNotificationPermission` step in the SDAI presentation layer.
     *
     * @return Result produced by `requestNotificationPermission`.
     * @author Dmitriy Moroz
     */
    override suspend fun requestNotificationPermission(): Boolean {
        if (PermissionUtil.checkNotificationPermission(context)) return true

        val result = CompletableDeferred<Boolean>()
        notificationPermissionResult.value = result
        var launched = false
        val granted = PermissionUtil.checkNotificationPermission(context) { permission ->
            launched = true
            launchNotificationPermission(permission)
        }
        if (granted) {
            notificationPermissionResult.value = null
            return true
        }
        if (!launched) {
            notificationPermissionResult.value = null
            return false
        }
        return result.await()
    }

    /**
     * Executes the `openUrl` step in the SDAI presentation layer.
     *
     * @param url remote URL used by the operation.
     * @author Dmitriy Moroz
     */
    override fun openUrl(url: String) {
        context.openUrl(url)
    }

    /**
     * Performs the SDAI side effect handled by `shareLogFile`.
     *
     * @author Dmitriy Moroz
     */
    override fun shareLogFile() {
        ReportProblemEmailComposer().invoke(context)
    }

    /**
     * Executes the `showDeveloperModeUnlocked` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun showDeveloperModeUnlocked() {
        context.showToast(Localization.string("debug_action_unlock"))
    }

    /**
     * Executes the `openAppSettings` step in the SDAI presentation layer.
     *
     * @author Dmitriy Moroz
     */
    override fun openAppSettings() {
        context.openAppSettings()
    }
}
